import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer

import scala.Console._

/**
  * using external file field to filter stuff out
  */
object ExternalFileField {

  var server: SolrClient = null

  def main(a: Array[String]) {

    try {
      val solrDir = ExternalFileField.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "external-field")

      val doc1 = new SolrInputDocument()
      doc1.addField("id", "prod1")
      doc1.addField("brand", "aaa")
      server.add(doc1)

      val doc2 = new SolrInputDocument()
      doc2.addField("id", "prod2")
      doc2.addField("brand", "aaa")

      server.add(doc2)

      val doc3 = new SolrInputDocument()
      doc3.addField("id", "prod3")
      doc3.addField("brand", "aaa")

      server.add(doc3)

      val doc4 = new SolrInputDocument()
      doc4.addField("id", "prod4")
      doc4.addField("brand", "aaa")
      server.add(doc4)

      val doc5 = new SolrInputDocument()
      doc5.addField("id", "prod5")
      doc5.addField("brand", "aaa")
      server.add(doc5)

      server.commit()

      val q = new ModifiableSolrParams()
      q.add("q", "*:*")
      q.add("fq", " {!frange l=1}account2 OR {!frange l=1}account1")
      val resp = server.query(q)
      println("---------------------------------------------")
      println(resp.getResults.getNumFound)
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
