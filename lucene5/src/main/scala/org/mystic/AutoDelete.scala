package org.mystic

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer

import scala.Console._
import scala.util.Random

/**
  * @see
  */
object AutoDelete {

  var server: SolrClient = null


  def main(a: Array[String]) {

    try {
      val rand = new Random()
      val solrDir = TypeAhead.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "auto-delete")

      val doc1 = new SolrInputDocument()
      doc1.addField("id", "1")
      server.add(doc1)

      val doc2 = new SolrInputDocument()
      doc2.addField("id", "2")
      server.add(doc2)

      val doc3 = new SolrInputDocument()
      doc3.addField("id", "3")
      server.add(doc3)

      server.commit()

      Thread.sleep(10000)

      val q1 = new ModifiableSolrParams()
      q1.add("q", "*:*")
      val res1 = server.query(q1).getResults

      for (i <-0 until res1.size()) {
        println(res1.get(i))
      }

      server.commit()

      Thread.sleep(10000)

      val q2 = new ModifiableSolrParams()
      q2.add("q", "*:*")
      val res2 = server.query(q2).getResults

      for (i <-0 until res2.size()) {
        println(res2.get(i))
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
