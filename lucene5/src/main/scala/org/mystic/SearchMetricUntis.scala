package org.mystic

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.common.{SolrDocumentList, SolrInputDocument}
import org.apache.solr.core.CoreContainer

import scala.Console._
import scala.collection.mutable
import scala.util.Random

/**
  * @see http://stackoverflow.com/q/41586031/2663985
  */
object SearchMetricUntis {

  var server: SolrClient = null


  def main(a: Array[String]) {

    try {
      val rand = new Random()
      val solrDir = TypeAhead.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "metric-units")

      val doc1 = new SolrInputDocument()
      doc1.addField("id", "1")
      doc1.addField("units", "screwdriver 10 mm")
      server.add(doc1)

      val doc2 = new SolrInputDocument()
      doc2.addField("id", "2")
      doc2.addField("units", "10mm")
      server.add(doc2)

      val doc3 = new SolrInputDocument()
      doc3.addField("id", "3")
      doc3.addField("units", "10 mm")
      server.add(doc3)

      server.commit()

      val q = new ModifiableSolrParams()
      q.add("q", "units:10mm")
      val res = server.query(q).getResults

      for (i <-0 until res.size()) {
        println(res.get(i))
      }



    } catch {
      case e: Exception => println(e)
    }
    finally {
      server.shutdown()
    }
    return
  }

}
