import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer
import org.apache.solr.client.solrj.SolrQuery

import scala.Console._

/**
  * @see https://stackoverflow.com/q/53587685/2663985
  */
object ElevatorProblem {

  var server: SolrClient = null

  def main(a: Array[String]) {

    try {
      val solrDir = ElevatorProblem.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "elevator")

      val q = new ModifiableSolrParams()
      q.add("q", "services")
      q.add("qt", "/select")
      q.add("qf", "content_en")
      q.add("fl", "content_en id score")
      q.add("exclusive", "true")


      val resp = server.query(q)
      println("---------------------------------------------")
      println(resp)
      println(resp.getResults.getNumFound)
      for (i <- 0 until resp.getResults.size()) {
        println(resp.getResults.get(i).get("score"))
        println(resp.getResults.get(i))
      }
      println("---------------------------------------------")

    } catch {
      case e: Exception => println(e)
    }
    finally {
      server.close()
    }
    return
  }

}
