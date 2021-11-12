import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer

import scala.Console._

/**
 * @see https://stackoverflow.com/q/53587685/2663985
 */
object BJQExample {

  var server: SolrClient = _

  def main(a: Array[String]): Unit = {

    try {
      val solrDir = Facets.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "bjq")

      val doc1 = new SolrInputDocument()
      doc1.addField("id", "1")
      doc1.addField("type", "type_dataset")
      doc1.addField("service_id", "service1")
      doc1.addField("data_id", "data1")
      val childDoc11 = new SolrInputDocument()
      childDoc11.addField("id", "1_1")
      childDoc11.addField("type", "type_column")
      childDoc11.addField("name", "random")
      doc1.addChildDocument(childDoc11)
      val childDoc12 = new SolrInputDocument()
      childDoc12.addField("id", "1_2")
      childDoc12.addField("type", "type_column")
      childDoc12.addField("name", "3242dfg43g3")
      doc1.addChildDocument(childDoc12)
      server.add(doc1)

      val doc2 = new SolrInputDocument()
      doc2.addField("id", "2")
      doc2.addField("type", "type_dataset")
      doc2.addField("service_id", "service2")
      doc2.addField("data_id", "data2")
      val childDoc2 = new SolrInputDocument()
      childDoc2.addField("id", "2_1")
      childDoc2.addField("type", "type_column")
      childDoc2.addField("name", "random")
      doc2.addChildDocument(childDoc2)
      server.add(doc2)

      val doc3 = new SolrInputDocument()
      doc3.addField("id", "3")
      doc3.addField("type", "type_dataset")
      doc3.addField("service_id", "service1")
      doc3.addField("data_id", "data2")
      val childDoc3 = new SolrInputDocument()
      childDoc3.addField("id", "3_1")
      childDoc3.addField("type", "type_column")
      childDoc3.addField("name", "random")
      doc2.addChildDocument(childDoc3)
      server.add(doc3)

      server.commit()

      val q = new ModifiableSolrParams()
      q.add("q", "+service_id:service1 +{!parent which=\"type:type_dataset\"}name:random")
      //      q.add("fq", "service_id:service1")
      //      q.add("fl", "*,greeting:[value v='hello']")
      q.add("fl", "*,[child parentFilter=type:type_dataset]")
      //      q.add("fl", "*,[child parentFilter=doc_type:book childFilter=doc_type:chapter limit=100]")
      //      q.add("fq", "type:type_dataset")
      val resp = server.query(q)
      println("---------------------------------------------")
      println(resp)
      println(resp.getResults.getNumFound)
      for (i <- 0 until resp.getResults.size()) {
        out.println(resp.getResults.get(i))
      }
      println("---------------------------------------------")

    } catch {
      case e: Exception => println(e)
    }
    finally {
      server.close()
    }
  }

}
