import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.client.solrj.request.GenericSolrRequest
import org.apache.solr.client.solrj.{SolrClient, SolrRequest}
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer

import java.util.concurrent.TimeUnit
import scala.Console._

/**
 * @see
 */
object XMLDIH {

  var server: SolrClient = _

  def main(a: Array[String]): Unit = {

    try {
      val solrDir = XMLDIH.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "xml-dih")

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
  }

}
