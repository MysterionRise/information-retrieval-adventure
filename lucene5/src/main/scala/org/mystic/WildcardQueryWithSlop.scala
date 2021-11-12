package org.mystic

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.common.{SolrDocumentList, SolrInputDocument}
import org.apache.solr.core.CoreContainer

import scala.Console._

/**
 * @see http://stackoverflow.com/q/26415156/2663985
 */
object WildcardQueryWithSlop {

  def main(a: Array[String]): Unit = {
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
          doc.addField("name_en", "play" + " week 5 later 2 space Bars Warehouse")
          doc.addField("name", "play" + " space space Bars Warehouse")
          doc.addField("genre", "play" + " later Bars Warehouse")
        } else if (i % 5 == 3) {
          doc.addField("name_en", "play1" + " week later space space Bars Warehouse")
          doc.addField("name", "play" + " space week Bars Warehouse")
          doc.addField("genre", "play" + " later space Bars Warehouse")
        } else {
          doc.addField("name_en", "play" + i + " space later Bars Warehouse")
          doc.addField("name", "play" + " week space Bars Warehouse")
          doc.addField("genre", "play" + " space space Bars Warehouse")
        }
        server.add(doc)
      })
      server.commit()

      val q = new ModifiableSolrParams()
      //      q.add("q", "lastName:play* AND lastName:'Bars Warehouse'")
      q.add("q", "week^5 later^2")
      q.add("defType", "edismax")
      q.add("pf", "name_en^15.0")
      q.add("qf", "name^10.0 genre^15.0")
      q.add("mm", "100")
      q.add("fl", "*, score")
      q.add("debugQuery", "true")
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
  }

}
