import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer

import scala.Console._
import scala.util.Random
import scalaj.http._

object SynonymGraph {

  var server: SolrClient = null

  def main(a: Array[String]) {

    try {
      val solrDir = SynonymGraph.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "synonym-graph")

      //      val response: HttpResponse[String] = Http("localhost:").param("q","monkeys").asString
      //      response.body
      //      response.code
      //      response.headers
      //      response.cookies

      //      val doc1 = new SolrInputDocument()
      //      doc1.addField("id", "1")
      //      doc1.addField("text", "teh small couch")
      //      server.add(doc1)
      //
      //      val doc2 = new SolrInputDocument()
      //      doc2.addField("id", "2")
      //      doc2.addField("text", "teh ginormous, humungous sofa")
      //      server.add(doc2)
      //
      //      val doc3 = new SolrInputDocument()
      //      doc3.addField("id", "3")
      //      doc3.addField("text", "divan")
      //      server.add(doc3)
      //
      //      val q1 = new ModifiableSolrParams()
      //      q1.add("q", "*:*")
      //      q1.add("fl", "*")
      //      val res1 = server.query(q1).getResults
      //
      //      for (i <- 0 until res1.size()) {
      //        println(res1.get(i))
      //      }

    } catch {
      case e: Exception => println(e)
    }
    finally {
      server.close()
    }
    return
  }

}
