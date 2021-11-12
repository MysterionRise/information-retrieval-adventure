import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient
import org.apache.solr.client.solrj.{SolrClient, SolrServerException}
import org.apache.solr.common.SolrInputDocument

import java.io.{FileInputStream, IOException}
import java.net.URL
import java.util
import javax.net.ssl.HttpsURLConnection
import javax.xml.stream.events.XMLEvent
import javax.xml.stream.{XMLEventReader, XMLInputFactory, XMLStreamException}
import scala.Console._

/**
 * https://stackoverflow.com/q/23639112/2663985
 */
object WikiIndexingTest {

  private final val MAX_SIZE: Int = 10000
  private final val THREAD_COUNT: Int = 10
  private final val QUEUE_SIZE = 10000
  private final val XML_FILE_PATH: String = "https://dumps.wikimedia.org/enwiki/latest/enwiki-latest-abstract1.xml"

  def main(a: Array[String]): Unit = {

    //    val trust: TrustManager = new X509TrustManager {
    //      override def checkServerTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = println()
    //
    //      override def checkClientTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = println()
    //
    //      override def getAcceptedIssuers: Array[X509Certificate] = return null
    //    }
    //
    //    val trustAllCerts = Array(trust)
    //
    //    val sc = SSLContext.getInstance("TLS")
    //    sc.init(null, trustAllCerts, new SecureRandom())
    //    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())

    // replace client if needed with
    //            val client = new HttpSolrClient.Builder().withBaseSolrUrl("http://localhost:8983/solr/wikipedia").allowCompression(true).build()
    val client = new ConcurrentUpdateSolrClient.Builder("http://localhost:8983/solr/getttingstarted").withThreadCount(THREAD_COUNT).withQueueSize(QUEUE_SIZE).build()
    client.deleteByQuery("*:*")
    client.commit()
    val start: Long = System.currentTimeMillis

    testIndexing(client)

    println("Indexing takes " + (System.currentTimeMillis - start) / 1000 + " seconds")
  }

  def testIndexing(client: SolrClient): Unit = {
    try {
      val xmlInputFactory: XMLInputFactory = XMLInputFactory.newInstance
      val con = new URL(XML_FILE_PATH).openConnection.asInstanceOf[HttpsURLConnection]
      //if you want to stream file from internet
      //      val xmlEventReader: XMLEventReader = xmlInputFactory.createXMLEventReader(con.getInputStream)
      val xmlEventReader: XMLEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream("D:\\enwiki-latest-abstract1.xml"))
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
        else if (event.isCharacters && isDocs && event.asCharacters.getData.trim.nonEmpty) {
          if ("title".equalsIgnoreCase(fieldName) || "url".equalsIgnoreCase(fieldName)) {
            doc.addField(fieldName, event.asCharacters.getData)
          }
        }
        else if (event.isEndElement) {
          if ("doc".equalsIgnoreCase(event.asEndElement.getName.getLocalPart)) {
            doc.addField("id", {
              id += 1
              id - 1
            })
            docs.add(doc)
            client.add(doc)
            doc.clear()
          }
        }
        xmlEventReader.nextEvent
        //        if (docs.size > MAX_SIZE) {
        //          println("---------- Adding " + MAX_SIZE + " documents to Solr---------------")
        //          client.add(docs)
        //          docs.clear
        //        }
      }
      println("-------------------Adding last chunk of docs to Solr-----------------")
      //      client.add(docs)
      client.commit
      client.close()
    }
    catch {
      case e: SolrServerException =>
        println("Error in Solr" + e.getRootCause)
      case e: IOException =>
        println("Error while IO operation" + e.getCause)
      case e: XMLStreamException =>
        println("Error while reading XML file" + e.getCause)
    }
  }
}
