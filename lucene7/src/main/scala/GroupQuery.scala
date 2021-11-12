import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer

import scala.Console._

/**
 * @see https://stackoverflow.com/q/47476770/2663985
 */
object GroupQuery {

  var server: SolrClient = _

  def main(a: Array[String]): Unit = {

    try {
      val solrDir = GroupQuery.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "group-query")

      val doc1 = new SolrInputDocument()
      doc1.addField("id", "1")
      doc1.addField("contact_id", "C1")
      doc1.addField("properties", "Green")
      doc1.addField("properties", "Blue")
      server.add(doc1)

      val doc2 = new SolrInputDocument()
      doc2.addField("id", "2")
      doc2.addField("contact_id", "C1")
      doc2.addField("properties", "Blue")
      doc2.addField("properties", "Yellow")
      server.add(doc2)

      val doc3 = new SolrInputDocument()
      doc3.addField("id", "3")
      doc3.addField("contact_id", "C2")
      doc3.addField("properties", "Green")
      doc3.addField("properties", "Yellow")
      server.add(doc3)

      server.commit()

      val q = new ModifiableSolrParams()
      q.add("q", "*:*")
      q.add("group", "true")
      q.add("group.query", "properties:Green")
      val resp = server.query(q).getGroupResponse
      println(resp.getValues.size())
      for (i <- 0 until resp.getValues.size()) {
        val command = resp.getValues.get(i)
        println(command.getName)
        println(command.getMatches)
        val values = command.getValues
        for (j <- 0 until values.size()) {
          println(values.get(j).getGroupValue)
          val result = values.get(j).getResult
          for (k <- 0 until result.size()) {
            println(result.get(k))
          }
        }
        println("-----")
      }

    } catch {
      case e: Exception => println(e)
    }
    finally {
      server.close()
    }
  }

}
