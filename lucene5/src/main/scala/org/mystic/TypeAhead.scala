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
  *
  */
object TypeAhead {

  var server: SolrClient = null

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

  def combineTitle(rand: Random): String = {
    val size = rand.nextInt(3) + 1
    val generatedTitle = (0 to size).foldRight("")((_, b: String) => b + " " + titles(rand.nextInt(titles.size))).trim
    possibleTitles.add(generatedTitle)
    generatedTitle
  }

  def addDocsToIndex(rand: Random) = {

    for (i <- 0 until 10000) {
      val doc = new SolrInputDocument()
      doc.addField("id", i)
      doc.addField("title", combineTitle(rand))
      server.add(doc)
    }
    server.commit()
  }

  def main(a: Array[String]) {

    try {
      val rand = new Random()
      val solrDir = TypeAhead.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      println(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "type-ahead")

      println(server.ping())

      addDocsToIndex(rand)

      // M approach
      for (i <- 0 until 10) {
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
      }

      // K approach
      for (i <- 0 until 10) {
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
      }

      // G approach
      for (i <- 0 until 10) {
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
      }



    }
    finally {
      server.shutdown()
    }
    return
  }

}
