package org.mystic

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer

import scala.Console._
import scala.util.Random

/**
  * @see http://stackoverflow.com/q/41924306/2663985
  */
object SearchArabicWords {

  var server: SolrClient = null


  def main(a: Array[String]) {

    try {
      val rand = new Random()
      val solrDir = TypeAhead.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "arabic-words")

      val doc1 = new SolrInputDocument()
      doc1.addField("id", "1")
      doc1.addField("text", " إ ")
      server.add(doc1)

      val doc2 = new SolrInputDocument()
      doc2.addField("id", "2")
      doc2.addField("text", " أ ")
      server.add(doc2)

      val doc3 = new SolrInputDocument()
      doc3.addField("id", "3")
      doc3.addField("text", " آ ")
      server.add(doc3)

      val doc4 = new SolrInputDocument()
      doc4.addField("id", "4")
      doc4.addField("text", " ا ")
      server.add(doc4)

      server.commit()

      val q = new ModifiableSolrParams()
      q.add("q", "text: ا ")
      val res = server.query(q).getResults

      for (i <- 0 until res.size()) {
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
