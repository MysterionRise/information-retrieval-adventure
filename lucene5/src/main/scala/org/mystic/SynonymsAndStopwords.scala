package org.mystic

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer

import scala.Console._

/**
 * @see http://stackoverflow.com/q/27128070/2663985
 * @see http://stackoverflow.com/q/28452690/2663985
 *
 *      +
 * @see http://stackoverflow.com/q/28667629/2663985
 */
object SynonymsAndStopwords {

  def main(args: Array[String]): Unit = {
    var server: SolrClient = null
    try {
      val solrDir = ManagedSchemaTesting.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "non-english-synonyms")
      println(server.ping())
      val doc1 = new SolrInputDocument()
      doc1.addField("id", "1")
      doc1.addField("ru", "spidermen")
      server.add(doc1)
      val doc2 = new SolrInputDocument()
      doc2.addField("id", "2")
      doc2.addField("ru", "stop word spiderman")
      server.add(doc2)
      val hindi = List("मैं भारत का रहने वाला हूँ", "मैं हिसंदुस्तान का रहने वाला  हूँ", "मैं india का रहने वाला हूँ", "मैं hindustan का रहने हूँ", "मैं bharat का रहने हूँ")
      var index = 2
      for (h <- hindi) {
        val doc = new SolrInputDocument()
        doc.addField("id", index)
        doc.addField("hi", h)
        index += 1
      }
      server.commit()
      val queries = List("spidermen", "superman", "batman", "бетмен", "бетмэн", "спайдермен", "спайдермэн", "супермен", "супермэн", "spiderman")
      val hindi_queries = List("india", "bharat", "भारत", "हिन्दुस्तान", "hindustan")
      for (query <- queries) {
        val q = new ModifiableSolrParams()
        println("-------=====================-------")
        q.add("q", "ru:" + query)
        println("query = " + q)
        val resp = server.query(q).getResults
        println("numFound = " + resp.size())
        for (i <- 0 until resp.size()) {
          println(resp.get(i))
        }
      }
      for (query <- hindi_queries) {
        val q = new ModifiableSolrParams()
        println("-------=====================-------")
        q.add("q", "hi:" + query)
        println("query = " + q)
        val resp = server.query(q).getResults
        println("numFound = " + resp.size())
        for (i <- 0 until resp.size()) {
          println(resp.get(i))
        }
      }
      val q = new ModifiableSolrParams()
      println("-------=====================-------")
      q.add("q", "ru:" + "stop word")
      println("query = " + q)
      val resp = server.query(q).getResults
      println("numFound = " + resp.size())
      for (i <- 0 until resp.size()) {
        println(resp.get(i))
      }
    } finally {
      server.shutdown()
    }
  }
}
