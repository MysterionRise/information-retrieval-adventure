package org.mystic

import org.apache.solr.client.solrj.{SolrServerException, SolrServer}
import org.apache.solr.client.solrj.impl.HttpSolrServer
import javax.xml.stream.{XMLStreamException, XMLEventReader, XMLInputFactory}
import java.net.URL
import org.apache.solr.common.SolrInputDocument
import scala.Predef.String
import javax.xml.stream.events.XMLEvent
import java.util
import java.io.IOException

/**
 * @author kperikov
 *         <p/>
 *         <p/>
 *         Connects to localhost Solr, and then index small part of russian wikipedia titles
 */
object Indexing {
  private final val XML_FILE_PATH: String = "http://dumps.wikimedia.org/ruwiki/latest/ruwiki-latest-abstract4.xml"
  private final val MAX_SIZE: Int = 1000

  def main(a: Array[String]) {
    val start: Long = System.currentTimeMillis
    val server: SolrServer = new HttpSolrServer("http://localhost:8983/solr/")
    try {
      server.deleteByQuery("*:*")
      val xmlInputFactory: XMLInputFactory = XMLInputFactory.newInstance
      val xmlEventReader: XMLEventReader = xmlInputFactory.createXMLEventReader(new URL(XML_FILE_PATH).openStream)
      val docs: util.Collection[SolrInputDocument] = new util.ArrayList[SolrInputDocument]
      var doc: SolrInputDocument = null
      var isDocs: Boolean = false
      var id: Long = 1
      var fieldName: String = ""
      System.out.println("-------------------Reading and parsing file " + XML_FILE_PATH + "-----------------")
      while (xmlEventReader.hasNext) {
        val event: XMLEvent = xmlEventReader.peek
        if (event.isStartElement) {
          if (isDocs) {
            fieldName = event.asStartElement.getName.getLocalPart
          }
          if ("doc".equalsIgnoreCase(event.asStartElement.getName.getLocalPart)) {
            isDocs = true
            doc = new SolrInputDocument
          }
        }
        else if (event.isCharacters && isDocs && !event.asCharacters.getData.trim.isEmpty) {
          if ("title".equalsIgnoreCase(fieldName) || "url".equalsIgnoreCase(fieldName)) {
            doc.addField(fieldName, event.asCharacters.getData)
          }
        }
        else if (event.isEndElement) {
          if ("doc".equalsIgnoreCase(event.asEndElement.getName.getLocalPart)) {
            doc.addField("id", ({
              id += 1;
              id - 1
            }))
            docs.add(doc)
          }
        }
        xmlEventReader.nextEvent
        if (docs.size > MAX_SIZE) {
          System.out.println("---------- Adding " + MAX_SIZE + " documents to Solr---------------")
          server.add(docs)
          server.commit
          docs.clear
        }
      }
      System.out.println("-------------------Adding last chunk of docs to Solr-----------------")
      server.add(docs)
      server.commit
      server.shutdown
      System.out.println("Indexing of " + id + " documents, takes " + (System.currentTimeMillis - start) / 1000 + " seconds")
    }
    catch {
      case e: SolrServerException => {
        System.out.println("Error in Solr" + e.getRootCause)
      }
      case e: IOException => {
        System.out.println("Error while IO operation" + e.getCause)
      }
      case e: XMLStreamException => {
        System.out.println("Error while reading XML file" + e.getCause)
      }
    }
  }
}
