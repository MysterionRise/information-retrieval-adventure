package org.mystic

import java.io.IOException
import java.net.URL
import java.util
import javax.xml.stream.events.XMLEvent
import javax.xml.stream.{XMLEventReader, XMLInputFactory, XMLStreamException}

import org.apache.solr.client.solrj.SolrServerException
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.core.CoreContainer

import scala.Console._

/**
 *         Connects to localhost Solr, and then index small part of russian wikipedia titles
 */
object WikiIndexing {
  private final val XML_FILE_PATH: String = "http://dumps.wikimedia.org/ruwiki/latest/ruwiki-latest-abstract4.xml"
  private final val MAX_SIZE: Int = 1000

  def main(a: Array[String]) {
    val start: Long = System.currentTimeMillis
    val solrDir = WikiIndexing.getClass.getResource("/solr").getPath
    val container = new CoreContainer(solrDir)
    container.load()
    val server = new EmbeddedSolrServer(container, "wikipedia")
    try {
      server.deleteByQuery("*:*")
      val xmlInputFactory: XMLInputFactory = XMLInputFactory.newInstance
      val xmlEventReader: XMLEventReader = xmlInputFactory.createXMLEventReader(new URL(XML_FILE_PATH).openStream)
      val docs: util.Collection[SolrInputDocument] = new util.ArrayList[SolrInputDocument]
      var doc: SolrInputDocument = null
      var isDocs: Boolean = false
      var id: Long = 1
      var fieldName: String = ""
      println("-------------------Reading and parsing file " + XML_FILE_PATH + "-----------------")
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
          println("---------- Adding " + MAX_SIZE + " documents to Solr---------------")
          server.add(docs)
          server.commit
          docs.clear
        }
      }
      println("-------------------Adding last chunk of docs to Solr-----------------")
      server.add(docs)
      server.commit
      server.shutdown
      println("Indexing of " + id + " documents, takes " + (System.currentTimeMillis - start) / 1000 + " seconds")
    }
    catch {
      case e: SolrServerException => {
        println("Error in Solr" + e.getRootCause)
      }
      case e: IOException => {
        println("Error while IO operation" + e.getCause)
      }
      case e: XMLStreamException => {
        println("Error while reading XML file" + e.getCause)
      }
    }
  }
}
