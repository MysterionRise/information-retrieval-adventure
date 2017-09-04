import java.util.Collections

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.client.solrj.request.FieldAnalysisRequest
import org.apache.solr.common.util.NamedList
import org.apache.solr.core.CoreContainer

import scala.Console._

/**
  * @see https://stackoverflow.com/q/45935681/2663985
  */
object DataRangeHotels {

  var server: SolrClient = null

  def main(a: Array[String]) {

    try {
      val solrDir = SynonymGraph.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "data-range")


    } catch {
      case e: Exception => println(e)
    }
    finally {
      server.close()
    }
    return
  }

}
