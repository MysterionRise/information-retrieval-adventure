package org.mystic

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.client.solrj.request.QueryRequest
import org.apache.solr.client.solrj.{SolrServer, SolrServerException}
import javax.xml.stream.{XMLStreamException, XMLEventReader, XMLInputFactory}
import java.net.URL
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer
import scala.Console._
import javax.xml.stream.events.XMLEvent
import java.util
import java.io.IOException

/**
 * @see  http://stackoverflow.com/q/26415156/2663985
 */
object ExactMatchTest {

  def main(a: Array[String]) {
    var server: SolrServer = null
    try {
      val solrDir = ExactMatchTest.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "test")
      println(server.ping())
      (0 until 100).foreach(i => {
        val doc = new SolrInputDocument()
        doc.addField("id", i)
        if (i % 5 == 0) {
          doc.addField("lastName", "John")
        } else if (i % 5 == 3) {
          doc.addField("lastName", "Johnson")
        } else {
          doc.addField("lastName", "Johns")
        }
        server.add(doc)
      })
      server.commit()

      val q = new ModifiableSolrParams()
      q.add("q", "lastNameExact:john")
      val resp = server.query(q).getResults
      for (i <- 0 until resp.size()) {
        out.println(resp.get(i))
      }
    }
    finally {
      server.shutdown()
    }
    return
  }

}