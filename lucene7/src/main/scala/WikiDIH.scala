import java.util.Collections
import java.util.concurrent.TimeUnit

import org.apache.solr.client.solrj.{SolrClient, SolrRequest}
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.client.solrj.request.{FieldAnalysisRequest, GenericSolrRequest}
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.common.util.NamedList
import org.apache.solr.core.CoreContainer

import scala.Console._
import scala.util.Random
import scalaj.http._

/**
  * @see https://stackoverflow.com/q/46993727/2663985
  */
object WikiDIH {

  var server: SolrClient = null

  def main(a: Array[String]) {

    try {
      val solrDir = WikiDIH.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "wiki-dih")

      println(server.request(new GenericSolrRequest(SolrRequest.METHOD.POST, "/dataimport", new ModifiableSolrParams()
        .add("command", "full-import")
        .add("synchronous", "true")
        .add("onError", "continue")
        .add("commit", "true"))))

      TimeUnit.SECONDS.sleep(5)

      println(server.request(new GenericSolrRequest(SolrRequest.METHOD.POST, "/dataimport", new ModifiableSolrParams().add("command", "status"))))

      TimeUnit.SECONDS.sleep(5)

      println(server.request(new GenericSolrRequest(SolrRequest.METHOD.POST, "/dataimport", new ModifiableSolrParams().add("command", "status"))))

      val q = new ModifiableSolrParams()
      q.add("q", "*:*")
      val resp = server.query(q).getResults
      println(resp.getNumFound)
      for (i <- 0 until resp.size()) {
        out.println(resp.get(i))
      }

    } catch {
      case e: Exception => println(e)
    }
    finally {
      server.close()
    }
    return
  }

}
