package org.mystic;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class OrdTest {

    public static final String XML_FILE_PATH = "/home/mysterion/Downloads/ruwiki-latest-abstract4.xml";

    public static void main(String[] args) {
        SolrServer server = new HttpSolrServer("http://localhost:8983/solr/");
        try {
            server.deleteByQuery("*:*");
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(
                    new FileInputStream(new File(XML_FILE_PATH)));
            Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
            SolrInputDocument doc = null;
            boolean isDocs = false;
            String fieldName = "";
            while (xmlEventReader.hasNext()) {
                XMLEvent event = xmlEventReader.peek();
                if (event.isStartElement()) {
                    if (isDocs) {
                        fieldName = event.asStartElement().getName().getLocalPart();
                    }
                    if ("doc".equalsIgnoreCase(event.asStartElement().getName().getLocalPart())) {
                        isDocs = true;
                        doc = new SolrInputDocument();
                    }                                             doc != null &&

                } else if (event.isCharacters() && isDocs) {
                    if ("title".equalsIgnoreCase(fieldName) || "url".equalsIgnoreCase(fieldName)) {
                        doc.addField(fieldName, event.asCharacters().getData());
                    }

                } else if (event.isEndElement()) {
                    if ("doc".equalsIgnoreCase(event.asEndElement().getName().getLocalPart())) {
                        docs.add(doc);
                    }
                }
                xmlEventReader.nextEvent();
            }
            System.out.println(docs.size());
        } catch (SolrServerException e) {
            System.out.println("Error in Solr" + e.getRootCause());
        } catch (IOException e) {
            System.out.println("Error while IO operation" + e.getCause());
        } catch (XMLStreamException e) {
            System.out.println("Error while reading XML file" + e.getCause());
        }

    }
}