package org.mystic

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.client.solrj.request.QueryRequest
import org.apache.solr.client.solrj.{SolrClient, SolrServer, SolrServerException}
import javax.xml.stream.{XMLStreamException, XMLEventReader, XMLInputFactory}
import java.net.URL
import org.apache.solr.common.{SolrDocumentList, SolrInputDocument}
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer
import scala.Console._
import javax.xml.stream.events.XMLEvent
import java.util
import java.io.IOException

/**
 * @see http://stackoverflow.com/q/26415156/2663985
 */
object WildcardQueryWithSlop {

  def main(a: Array[String]) {
    var server: SolrClient = null
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
          doc.addField("test", "play" + " space space Bars Warehouse")
        } else if (i % 5 == 3) {
          doc.addField("test", "play1" + " space space Bars Warehouse")
        } else {
          doc.addField("test", "play" + i + " space space Bars Warehouse")
        }
        server.add(doc)
      })
      server.commit()

      val q = new ModifiableSolrParams()
//      q.add("q", "lastName:play* AND lastName:'Bars Warehouse'")
      q.add("q", "test:\"Warehouse\"")
      val results: SolrDocumentList = server.query(q).getResults
      out.println(results.getNumFound)
      val resp = results
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
