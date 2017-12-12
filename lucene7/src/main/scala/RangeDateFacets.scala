import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer

import scala.Console._

/**
  * @see https://stackoverflow.com/q/47761539/2663985
  */

object RangeDateFacets {

  var server: SolrClient = null

  def main(a: Array[String]) {

    try {
      val solrDir = RangeDateFacets.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "range-facets")

      val doc1 = new SolrInputDocument()
      doc1.addField("id", "1")
      doc1.addField("active", "[2017-12-01T00:00:00.000Z TO 2017-12-04T00:00:00.000Z]")
      server.add(doc1)

      val doc2 = new SolrInputDocument()
      doc2.addField("id", "2")
      doc2.addField("active", "[2017-12-02T00:00:00.000Z TO 2017-12-04T00:00:00.000Z]")
      server.add(doc2)

      val doc3 = new SolrInputDocument()
      doc3.addField("id", "3")

      doc3.addField("active", "[2017-12-02T00:00:00.000Z TO 2017-12-06T00:00:00.000Z]")
      server.add(doc3)

      server.commit()

      val q = new ModifiableSolrParams()
      q.add("q", "*:*")
      q.add("facet", "true")
      q.add("facet.range", "active")
      q.add("facet.range.start", "NOW/MONTH")
      q.add("facet.range.end", "NOW/MONTH+1MONTH")
      q.add("facet.range.gap", "+1DAY")
      var resp = server.query(q)
      println(resp.getResults.getNumFound)
      val ranges = resp.getFacetRanges
      for (i <- 0 until ranges.size()) {
        val value = ranges.get(i)
        println(value.getName)
        val counts = value.getCounts
        for (j <- 0 until counts.size()) {
          println(counts.get(j).getValue)
          println(counts.get(j).getCount)
        }
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

