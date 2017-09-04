import java.io.File
import java.util.Collections

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.client.solrj.request.{ContentStreamUpdateRequest, FieldAnalysisRequest}
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.common.util.NamedList
import org.apache.solr.core.CoreContainer

import scala.Console._

/**
  * @see https://stackoverflow.com/q/46037040/2663985
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
