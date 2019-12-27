import java.io.{FileInputStream, IOException}
import java.nio.file.{Files, Paths}
import java.util
import java.util.concurrent.TimeUnit

import javax.xml.stream.events.XMLEvent
import javax.xml.stream.{XMLEventReader, XMLInputFactory, XMLStreamException}
import org.apache.solr.client.solrj.impl.{ConcurrentUpdateSolrClient, HttpSolrClient}
import org.apache.solr.client.solrj.{SolrClient, SolrServerException}
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams

import scala.Console._

object WikiIndexingTest {

  private var MAX_SIZE: Int = 10000
  private var THREAD_COUNT: Int = 10
  private var QUEUE_SIZE = 10000
  private var XML_FILE_PATH: String = "/Users/konstantinp/Downloads/enwiki-latest-abstract1.xml"
  private var SOLR_URL = "http://localhost:8983/solr/gettingstarted"
  private var COMMAND = "index"
  private var TYPE = "legacy"
  private var SLAVES = "ip1, ip2, ip3"

  def main(a: Array[String]) {
    if (Files.exists(Paths.get("config.env"))) {
      val lines = Files.readAllLines(Paths.get(".", "config.env"))
      MAX_SIZE = Integer.parseInt(lines.get(0))
      THREAD_COUNT = Integer.parseInt(lines.get(1))
      QUEUE_SIZE = Integer.parseInt(lines.get(2))
      XML_FILE_PATH = lines.get(3)
      SOLR_URL = lines.get(4)
      COMMAND = lines.get(5)
      TYPE = lines.get(6)
      SLAVES = lines.get(7)
    } else {
      println("No config file was found - default settings are used")
    }
    val client = new ConcurrentUpdateSolrClient.Builder(SOLR_URL)
      .withThreadCount(THREAD_COUNT)
      .withQueueSize(QUEUE_SIZE)
      .build()

    COMMAND match {
      case "index" => testIndexing(client)
      case "reindex" => testReindexing(client)
      case _ => throw new IllegalArgumentException(s"Command ${COMMAND} is unknown")
    }


  }

  def checkSlaves(slavesIps: List[String], expectedDocs: Long, port: Int = 8080): Unit = {
    slavesIps.forall(ip => waitTillExpectedNumberOfDocs(
      new HttpSolrClient.Builder(s"http://${ip}:${port}/solr/gettingstarted").build(),
      expectedDocs
    ))
  }

  def waitTillExpectedNumberOfDocs(client: SolrClient, expectedDocs: Long, collectionName: String = "gettingstarted"): Boolean = {
    var found = 0L
    while (found != expectedDocs) {
      TimeUnit.MILLISECONDS.sleep(100)
      found = getNumberOfDocs(client, collectionName)

    }
    return true
  }

  def getNumberOfDocs(client: SolrClient, collectionName: String = "gettingstarted"): Long = {
    val q = new ModifiableSolrParams()
    q.add("q", "*:*")
    client.query(collectionName, q)
    val response = client.query(q).getResults
    return response.getNumFound
  }

  def createListFromString(SLAVES: String): List[String] = {
    return SLAVES.split(",").map(_.trim).toList
  }

  def testReindexing(client: SolrClient): Unit = {
    val start: Long = System.currentTimeMillis

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
          // TODO add more fields
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

      println("Indexing takes " + (System.currentTimeMillis - start) / 1000 + " seconds")

      val replicationStart = System.currentTimeMillis

      val expectedDocsCount = getNumberOfDocs(client)

      client.close()

      println("-------------------Waiting on replication to complete-----------------")
      TYPE match {
        case "legacy" => checkSlaves(createListFromString(SLAVES), expectedDocsCount)
      }

      println("--------Replication takes " + (System.currentTimeMillis - replicationStart) / 1000 + " seconds--------")


      println("--------Total: " + (System.currentTimeMillis - start) / 1000 + " seconds--------")
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

  def testIndexing(client: SolrClient): Unit = {
    client.deleteByQuery("*:*")
    client.commit()

    testReindexing(client)

  }
}
