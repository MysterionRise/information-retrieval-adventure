package org.mystic

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.client.solrj.response.UpdateResponse
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer

import scala.Console._
import scala.collection.mutable
import scala.util.Random

/**
 *
 */
object TypeAhead {

  val titles = List(
    "calvin",
    "klein",
    "jeans",
    "pants",
    "lacoste",
    "shirt",
    "skirt",
    "red",
    "adidas",
    "puma",
    "blue",
    "black",
    "yellow",
    "silver",
    "hat",
    "nike",
    "socks"
  )
  val possibleTitles = new mutable.HashSet[String]()
  var server: SolrClient = _

  def main(a: Array[String]): Unit = {

    try {
      val rand = new Random()
      val solrDir = TypeAhead.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "type-ahead")

      addDocsToIndex(rand)

      println("M approach")
      for (i <- 0 until 100) {
        val title = possibleTitles.toList.apply(rand.nextInt(possibleTitles.size))
        val query = title.substring(0, rand.nextInt(title.length / 2) + 5)
        val q = new ModifiableSolrParams()
        val queryS = query.split(" ").toList.foldLeft("")((pos: String, b: String) => s"$pos +$b") + "*"
        q.add("q", s"""title_prefix:(${queryS.trim})""")
        val resp = server.query(q).getResults
        println(s"title = $title")
        println(s"query = ${queryS.trim}")
        println(s"numFound = ${resp.getNumFound}")
        assert(resp.getNumFound > 0)
        for (i <- 0 until resp.size()) {
          println(resp.get(i))
        }
      }


      println("K approach")
      for (i <- 0 until 100) {
        val title = possibleTitles.toList.apply(rand.nextInt(possibleTitles.size))
        var query = ""
        if (rand.nextBoolean()) {
          query = title.substring(0, rand.nextInt(title.length / 2) + 5).trim
        } else {
          val of = title.indexOf(" ")
          query = title.substring(of, Math.min(title.length, title.length / 2 + 5)).trim
        }
        val q = new ModifiableSolrParams()
        q.add("q", s"""title_ngram:"$query"""")
        val resp = server.query(q).getResults
        println(s"title = $title")
        println(s"query = $query")
        println(s"numFound = ${resp.getNumFound}")
        assert(resp.getNumFound > 0)
        for (i <- 0 until resp.size()) {
          println(resp.get(i))
        }
      }

      /*println("G approach")
      for (i <- 0 until 5) {
        val title = possibleTitles.toList.apply(rand.nextInt(possibleTitles.size))
        val query = title.substring(0, rand.nextInt(title.length / 2) + 5)
        val q = new ModifiableSolrParams()
        val queryS = query.split(" ").toList.foldLeft("")((pos: String, b: String) => s"$pos +$b") + "*"
        q.add("q", s"""title_prefix:(${queryS.trim})""")
        val resp = server.query(q).getResults
        println(s"query = ${queryS.trim}")
        println(s"numFound = ${resp.size()}")
        for (i <- 0 until resp.size()) {
          println(resp.get(i))
        }
      }*/


    } catch {
      case e: Exception => println(e)
    }
    finally {
      server.shutdown()
    }
  }

  def addDocsToIndex(rand: Random): UpdateResponse = {

    for (i <- 0 until 10000) {
      val doc = new SolrInputDocument()
      doc.addField("id", i)
      doc.addField("title", combineTitle(rand))
      server.add(doc)
    }
    server.commit()
  }

  def combineTitle(rand: Random): String = {
    val size = rand.nextInt(3) + 1
    val generatedTitle = (0 to size).foldRight("")((_, b: String) => b + " " + titles(rand.nextInt(titles.size))).trim
    possibleTitles.add(generatedTitle)
    generatedTitle
  }

}
