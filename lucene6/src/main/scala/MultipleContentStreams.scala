import java.io.File

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer

import scala.Console._

/**
  * @see https://stackoverflow.com/q/46037040/2663985
  *      It's actually not possible to index multiple files, they will be stored under different id or even will rewrite itself
  */
object MultipleContentStreams {

  var server: SolrClient = null

  def main(a: Array[String]) {

    try {
      val solrDir = SynonymGraph.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "multiple-content-streams")

      val req = new ContentStreamUpdateRequest("/update/extract")
      req.setParam("literal.id", "1")
      req.addFile(new File("/home/kperikov/projects/information-retrieval-adventure/lucene6/src/main/resources/1.txt"), "")
      req.addFile(new File("/home/kperikov/projects/information-retrieval-adventure/lucene6/src/main/resources/2.txt"), "")
      server.request(req)
      server.commit()

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
