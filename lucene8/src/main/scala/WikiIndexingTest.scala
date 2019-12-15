import java.io.{FileInputStream, IOException}
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util

import javax.net.ssl.{HttpsURLConnection, SSLContext, TrustManager, X509TrustManager}
import javax.xml.stream.events.XMLEvent
import javax.xml.stream.{XMLEventReader, XMLInputFactory, XMLStreamException}
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient
import org.apache.solr.client.solrj.{SolrClient, SolrServerException}
import org.apache.solr.common.SolrInputDocument

import scala.Console._

/**
  *
  */
object WikiIndexingTest {

  private final val MAX_SIZE: Int = 10000
  private final val THREAD_COUNT: Int = 10
  private final val QUEUE_SIZE = 10000

  def main(a: Array[String]) {

    val client = new ConcurrentUpdateSolrClient.Builder("http://localhost:8983/solr/gettingstarted")
      .withThreadCount(THREAD_COUNT)
      .withQueueSize(QUEUE_SIZE)
      .build()
    client.deleteByQuery("*:*")
    client.commit()
    val start: Long = System.currentTimeMillis

    testIndexing(client)

    println("Indexing takes " + (System.currentTimeMillis - start) / 1000 + " seconds")
  }

  private final val XML_FILE_PATH: String = "/Users/konstantinp/Downloads/enwiki-latest-abstract1.xml"

  def testIndexing(client: SolrClient): Unit = {
    try {
      val xmlInputFactory: XMLInputFactory = XMLInputFactory.newInstance
      val xmlEventReader: XMLEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(XML_FILE_PATH))
      val docs: util.Collection[SolrInputDocument] = new util.ArrayList[SolrInputDocument]
      var isDocs: Boolean = false
      var id: Long = 1
      var fieldName: String = ""
      var doc: SolrInputDocument = null
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
          // TODO
          if ("title".equalsIgnoreCase(fieldName) || "url".equalsIgnoreCase(fieldName) || "abstract".equalsIgnoreCase(fieldName)) {
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
            //            client.add(doc)
            //            doc.clear()
          }
        }
        xmlEventReader.nextEvent
        if (docs.size > MAX_SIZE) {
          println("---------- Adding " + MAX_SIZE + " documents to Solr---------------")
          client.add(docs)
          docs.clear
        }
      }
      println("-------------------Adding last chunk of docs to Solr-----------------")
      client.add(docs)
      client.commit
      client.close()
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
