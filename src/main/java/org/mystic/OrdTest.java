package org.mystic;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

public class OrdTest {

    // could remove 4, to get the biggest file to analyze
    public static final String XML_FILE_PATH = "http://dumps.wikimedia.org/ruwiki/latest/ruwiki-latest-abstract4.xml";
    private static final int MAX_SIZE = 1000;

    public static void main(String[] args) {
        SolrServer server = new HttpSolrServer("http://localhost:8983/solr/");
        try {
            server.deleteByQuery("*:*");
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new URL(XML_FILE_PATH).openStream());
            Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
            SolrInputDocument doc = null;
            boolean isDocs = false;
            long id = 1;
            String fieldName = "";
            System.out.println("-------------------Reading and parsing file " + XML_FILE_PATH + "-----------------");
            while (xmlEventReader.hasNext()) {
                XMLEvent event = xmlEventReader.peek();
                if (event.isStartElement()) {
                    if (isDocs) {
                        fieldName = event.asStartElement().getName().getLocalPart();
                    }
                    if ("doc".equalsIgnoreCase(event.asStartElement().getName().getLocalPart())) {
                        isDocs = true;
                        doc = new SolrInputDocument();
                    }

                } else if (event.isCharacters() && isDocs) {
                    if ("title".equalsIgnoreCase(fieldName) || "url".equalsIgnoreCase(fieldName)) {
                        doc.addField(fieldName, event.asCharacters().getData());
                    }

                } else if (event.isEndElement()) {
                    if ("doc".equalsIgnoreCase(event.asEndElement().getName().getLocalPart())) {
                        doc.addField("id", id++);
                        docs.add(doc);
                    }
                }
                xmlEventReader.nextEvent();
                if (docs.size() > MAX_SIZE) {
                    System.out.println("---------- Adding " + MAX_SIZE + " documents to Solr---------------");
                    server.add(docs);
                    server.commit();
                    docs.clear();
                }
            }
            System.out.println("-------------------Adding last chunk of docs to Solr-----------------");
            server.add(docs);
            server.commit();
        } catch (SolrServerException e) {
            System.out.println("Error in Solr" + e.getRootCause());
        } catch (IOException e) {
            System.out.println("Error while IO operation" + e.getCause());
        } catch (XMLStreamException e) {
            System.out.println("Error while reading XML file" + e.getCause());
        }

    }
}