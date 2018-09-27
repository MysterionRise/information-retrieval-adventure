package org.mystic

import java.util.concurrent.TimeUnit

import org.apache.commons.lang.RandomStringUtils
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer

import scala.Console._


object ICUPerformance {

  var server: SolrClient = null


  def main(a: Array[String]) {

    try {

      val solrDir = ICUPerformance.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "icu-speed")

      val numberOfQueries = 100000
      val numberOfDocs = 100000
      for (i <- 0 until numberOfDocs) {
        val doc1 = new SolrInputDocument()
        doc1.addField("id", i.toString)
        doc1.addField("text", RandomStringUtils.random(10000, 0, 20000, true, true))
        if (i % 1234 == 0)
          server.commit()
        server.add(doc1)
      }

      server.commit()
      server.optimize()

      TimeUnit.MINUTES.sleep(5)

      val start = System.currentTimeMillis()

      for (_ <- 0 until numberOfQueries) {
        val q = new ModifiableSolrParams()
        q.add("q", "text:" + RandomStringUtils.random(20, 0, 20000, true, true))
        val res = server.query(q).getResults
        println(res.size())
      }

      println("-------------------------------------------")
      val duration = System.currentTimeMillis() - start
      println(duration)
      println((1.0f * duration) / numberOfQueries)
      println("-------------------------------------------")

    } catch {
      case e: Exception => println(e)
    }
    finally {
      server.shutdown()
    }
    return
  }

}
