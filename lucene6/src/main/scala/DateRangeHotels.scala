import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer

import scala.Console._

/**
 * @see https://stackoverflow.com/q/45935681/2663985
 */
object DateRangeHotels {

  var server: SolrClient = _

  def main(a: Array[String]): Unit = {

    try {
      val solrDir = SynonymGraph.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "date-range")

      val doc1 = new SolrInputDocument()
      doc1.addField("id", "1")
      doc1.addField("text", "hotel1")
      doc1.addField("date", "[2017-01-01T00:00:00Z TO 2017-12-31T00:00:00Z]")
      server.add(doc1)

      val doc2 = new SolrInputDocument()
      doc2.addField("id", "2")
      doc2.addField("text", "hotel2")
      doc2.addField("date", "[2017-01-01T00:00:00Z TO 2018-01-01T00:00:00Z]")
      server.add(doc2)

      val doc3 = new SolrInputDocument()
      doc3.addField("id", "3")
      doc3.addField("text", "hotel3")
      doc3.addField("date", "[2017-01-01T00:00:00Z TO 2017-12-31T23:59:59Z]")
      server.add(doc3)

      server.commit()

      //fq={!field f=dateRange op=Contains}[2017 TO 2017]
      val q = new ModifiableSolrParams()
      q.add("q", "*:*")
      q.add("fq", "{!field f=date op=Within}[2017 TO 2017]")
      val res = server.query(q).getResults

      for (i <- 0 until res.size()) {
        println(res.get(i))
      }

    } catch {
      case e: Exception => println(e)
    }
    finally {
      server.close()
    }
  }

}
