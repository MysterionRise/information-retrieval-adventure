import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer

import scala.Console._

/**
 * @see https://stackoverflow.com/q/47591065/2663985
 */
object Facets {

  var server: SolrClient = _

  def main(a: Array[String]): Unit = {

    try {
      val solrDir = Facets.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "facets")

      val doc1 = new SolrInputDocument()
      doc1.addField("id", "1")
      doc1.addField("brand", "Aaaa")
      server.add(doc1)

      val doc2 = new SolrInputDocument()
      doc2.addField("id", "2")
      doc2.addField("brand", "Bbbb")
      server.add(doc2)

      val doc3 = new SolrInputDocument()
      doc3.addField("id", "3")
      doc3.addField("brand", "Cccc")
      server.add(doc3)

      val doc4 = new SolrInputDocument()
      doc4.addField("id", "4")
      doc4.addField("brand", "abbb")
      server.add(doc4)

      val doc5 = new SolrInputDocument()
      doc5.addField("id", "5")
      doc5.addField("brand", "aBbb")
      server.add(doc5)

      server.commit()

      val q = new ModifiableSolrParams()
      q.add("q", "*:*")
      q.add("facet", "true")
      q.add("facet.field", "brand")
      q.add("facet.field", "brand_text")
      q.add("facet.sort", "index")
      val resp = server.query(q)
      println("---------------------------------------------")
      println(resp.getResults.getNumFound)
      println(resp.getFacetFields)
      println("---------------------------------------------")

    } catch {
      case e: Exception => println(e)
    }
    finally {
      server.close()
    }
  }

}
