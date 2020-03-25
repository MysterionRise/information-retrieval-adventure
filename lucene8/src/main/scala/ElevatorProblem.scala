import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer

import scala.Console._

/**
 * @see https://stackoverflow.com/q/53587685/2663985
 */
object ElevatorProblem {

  var server: SolrClient = null

  def main(a: Array[String]) {

    try {
      val solrDir = ElevatorProblem.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "elevator")

      //      metadata_en
      val doc1 = new SolrInputDocument()
      doc1.addField("id", "1")
      doc1.addField("crxPath", "1")
      doc1.addField("metadata_en", "priority")
      doc1.addField("site", "ae_en")
      server.add(doc1)

      val doc2 = new SolrInputDocument()
      doc2.addField("id", "2")
      doc2.addField("crxPath", "1")
      doc2.addField("metadata_en", "testpriority")
      doc2.addField("site", "ae_en")
      server.add(doc2)

      val doc3 = new SolrInputDocument()
      doc3.addField("id", "3")
      doc3.addField("crxPath", "1")
      doc3.addField("metadata_en", "Priority")
      doc3.addField("site", "ae_es")
      server.add(doc3)

      val doc4 = new SolrInputDocument()
      doc4.addField("id", "4")
      doc4.addField("crxPath", "1")
      doc4.addField("metadata_en", "Priority")
      doc4.addField("site", "ae_en")
      server.add(doc4)
      server.commit()

      val suggesterQuery = new ModifiableSolrParams()
      suggesterQuery.add("qt", "/suggest")
      suggesterQuery.add("suggest", "true")
      suggesterQuery.add("suggest.build", "true")
      suggesterQuery.add("suggest.dictionary", "suggester_en")
      suggesterQuery.add("suggest.q", "Prio")
      suggesterQuery.add("suggest.cfq", "ae_en")


      val suggestResp = server.query(suggesterQuery)
      println("-------------suggest query--------------------------------")
      println(suggestResp)

      val elevatorQuery = new ModifiableSolrParams()
      elevatorQuery.add("q", "services")
      elevatorQuery.add("qt", "/select")
      elevatorQuery.add("qf", "content_en")
      elevatorQuery.add("fl", "content_en id score")
      elevatorQuery.add("exclusive", "true")

      val elevatorResp = server.query(elevatorQuery)
      println("-------------suggest query--------------------------------")
      println(elevatorResp)

      println(elevatorResp.getResults.getNumFound)
      for (i <- 0 until elevatorResp.getResults.size()) {
        println(elevatorResp.getResults.get(i).get("score"))
        println(elevatorResp.getResults.get(i))
      }
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
