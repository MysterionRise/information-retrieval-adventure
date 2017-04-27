import java.util.Collections

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.client.solrj.request.FieldAnalysisRequest
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.common.util.NamedList
import org.apache.solr.core.CoreContainer

import scala.Console._
import scala.util.Random
import scalaj.http._

object SynonymGraph {

  var server: SolrClient = null

  def main(a: Array[String]) {

    try {
      val solrDir = SynonymGraph.getClass.getResource("/solr").getPath.substring(1)
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "synonym-graph")

      val resp: NamedList[Object] = server.request(new FieldAnalysisRequest().setFieldTypes(Collections.singletonList("text")).setFieldValue("teh huge sofa"))
      val it = resp.iterator()
      while (it.hasNext) {
        println(it.next())
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
