import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer

import scala.Console._

/**
  * @see https://stackoverflow.com/q/47348712/2663985
  */
object SynonymsWithBrackets {

  var server: SolrClient = null

  def main(a: Array[String]) {

    try {
      val solrDir = SynonymsWithBrackets.getClass.getResource("/solr").getPath.substring(1)
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "wiki-dih")

      val doc1 = new SolrInputDocument()
      doc1.addField("id", "1")
      doc1.addField("text", "test doc (cat) dog")
      server.add(doc1)

      val doc2 = new SolrInputDocument()
      doc2.addField("id", "2")
      doc2.addField("text", "test doc dog cat")
      server.add(doc2)

      val doc3 = new SolrInputDocument()
      doc3.addField("id", "3")
      doc3.addField("text", "test doc (cat) cat")
      server.add(doc3)

      val q = new ModifiableSolrParams()
      q.add("q", "text:dog")
      var resp = server.query(q).getResults
      println(resp.getNumFound)
      for (i <- 0 until resp.size()) {
        out.println(resp.get(i))
      }

      q.clear()
      q.add("q", "text:(cat)")
      resp = server.query(q).getResults
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
