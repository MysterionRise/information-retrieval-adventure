import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.client.solrj.response.RangeFacet
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer

import java.util
import scala.Console._

/**
 * @see https://stackoverflow.com/q/47761539/2663985
 * @see https://stackoverflow.com/q/47802286/2663985
 */

object RangeDateFacets {

  var server: SolrClient = _

  def main(a: Array[String]): Unit = {

    try {
      val solrDir = RangeDateFacets.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "range-facets")

      val doc1 = new SolrInputDocument()
      doc1.addField("id", "1")
      doc1.addField("active", "[2017-12-01T00:00:00.000Z TO 2017-12-04T00:00:00.000Z]")
      doc1.addField("interests", "hockey")
      doc1.addField("interests", "soccer")
      server.add(doc1)

      val doc2 = new SolrInputDocument()
      doc2.addField("id", "2")
      doc2.addField("active", "[2017-12-02T00:00:00.000Z TO 2017-12-04T00:00:00.000Z]")
      doc2.addField("interests", "cricket")
      doc2.addField("interests", "soccer")
      server.add(doc2)

      val doc3 = new SolrInputDocument()
      doc3.addField("id", "3")
      doc3.addField("active", "[2017-12-02T00:00:00.000Z TO 2017-12-06T00:00:00.000Z]")
      doc3.addField("interests", "hockey")
      doc3.addField("interests", "cricket")
      server.add(doc3)

      server.commit()

      val q = new ModifiableSolrParams()
      q.add("q", "*:*")
      q.add("facet", "true")
      q.add("facet.range", "{!tag=r1}active")
      q.add("facet.range.start", "NOW/MONTH")
      q.add("facet.range.end", "NOW/MONTH+5DAYS")
      q.add("facet.range.include", "outer")
      q.add("facet.range.gap", "+1DAY")
      q.add("facet.pivot", "{!range=r1}interests")
      val resp = server.query(q)
      println(resp.getResults.getNumFound)
      val pivot = resp.getFacetPivot
      for (i <- 0 until pivot.size()) {
        println(pivot.getName(i))
        val fields = pivot.getVal(i)
        for (j <- 0 until fields.size()) {
          print(fields.get(j).getValue + " ")
          println(fields.get(j).getCount)
          printRangeFacets(fields.get(j).getFacetRanges)
        }
      }
      val ranges = resp.getFacetRanges
      printRangeFacets(ranges)

    } catch {
      case e: Exception => println(e)
    }
    finally {
      server.close()
    }
  }

  private def printRangeFacets(ranges: util.List[RangeFacet[_, _]]): Unit = {
    for (i <- 0 until ranges.size()) {
      val value = ranges.get(i)
      println(value.getName)
      val counts = value.getCounts
      for (j <- 0 until counts.size()) {
        println(counts.get(j).getValue)
        println(counts.get(j).getCount)
      }
    }
  }
}

