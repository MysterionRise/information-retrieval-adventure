import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.client.solrj.request.FieldAnalysisRequest
import org.apache.solr.common.util.NamedList
import org.apache.solr.core.CoreContainer

import java.util.Collections
import scala.Console._

object SynonymGraph {

  var server: SolrClient = _

  def main(a: Array[String]): Unit = {

    try {
      val solrDir = SynonymGraph.getClass.getResource("/solr").getPath
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
  }

}
