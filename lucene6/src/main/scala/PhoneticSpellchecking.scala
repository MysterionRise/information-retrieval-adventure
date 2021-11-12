import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer

import java.util.concurrent.TimeUnit
import scala.Console._

/**
 * @see https://stackoverflow.com/q/20350714/2663985
 */
object PhoneticSpellchecking {

  var server: SolrClient = _

  def main(a: Array[String]): Unit = {

    try {
      val solrDir = SynonymGraph.getClass.getResource("/solr").getPath.substring(1)
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "phonetic-spellchecking")

      val doc1 = new SolrInputDocument()
      doc1.addField("id", "1")
      doc1.addField("title", "телевизор samsung")
      server.add(doc1)

      val doc2 = new SolrInputDocument()
      doc2.addField("id", "2")
      doc2.addField("title", "televizor самсунг")
      server.add(doc2)

      val doc3 = new SolrInputDocument()
      doc3.addField("id", "3")
      doc3.addField("title", "гэлакси samsung")
      server.add(doc3)

      val doc4 = new SolrInputDocument()
      doc4.addField("id", "4")
      doc4.addField("title", "galaxy самсунг")
      server.add(doc4)

      server.commit()

      //fq={!field f=dateRange op=Contains}[2017 TO 2017]
      val q = new ModifiableSolrParams()
      q.add("q", "title:tilevizor")
      var res = server.query(q).getResults
      println(q)
      for (i <- 0 until res.size()) {
        println(res.get(i))
      }

      TimeUnit.SECONDS.sleep(1)

      q.clear()
      q.add("q", "title:тилевизор")
      res = server.query(q).getResults
      println(q)
      for (i <- 0 until res.size()) {
        println(res.get(i))
      }

      TimeUnit.SECONDS.sleep(1)

      q.clear()
      q.add("q", "title:smasung")
      res = server.query(q).getResults
      println(q)
      for (i <- 0 until res.size()) {
        println(res.get(i))
      }

      TimeUnit.SECONDS.sleep(1)

      q.clear()
      q.add("q", "title:caмсунг")
      res = server.query(q).getResults
      println(q)
      for (i <- 0 until res.size()) {
        println(res.get(i))
      }

      TimeUnit.SECONDS.sleep(1)

      q.clear()
      q.add("q", "title:галакси")
      res = server.query(q).getResults
      println(q)
      for (i <- 0 until res.size()) {
        println(res.get(i))
      }

      q.clear()
      q.add("q", "title:гэлакси")
      res = server.query(q).getResults
      println(q)
      for (i <- 0 until res.size()) {
        println(res.get(i))
      }

      TimeUnit.SECONDS.sleep(1)

    } catch {
      case e: Exception => println(e)
    }
    finally {
      server.close()
    }
  }

}
