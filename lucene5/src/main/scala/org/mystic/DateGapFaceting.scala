package org.mystic

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.client.solrj.response.RangeFacet
import org.apache.solr.client.solrj.response.RangeFacet.Date
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.common.{SolrDocumentList, SolrInputDocument}
import org.apache.solr.core.CoreContainer

import scala.Console._
import scala.collection.mutable
import scala.util.Random

/**
  * @see http://stackoverflow.com/q/41856051/2663985
  */
object DateGapFaceting {

  var server: SolrClient = null


  def main(a: Array[String]) {

    try {
      val rand = new Random()
      val solrDir = DateGapFaceting.getClass.getResource("/solr").getPath.substring(1)
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "date-gap")

      val doc1 = new SolrInputDocument()
      doc1.addField("id", "1")
      doc1.addField("date", "2017-01-01T00:00:00Z")
      server.add(doc1)

      val doc2 = new SolrInputDocument()
      doc2.addField("id", "2")
      doc2.addField("date", "2017-01-02T00:00:00Z")
      server.add(doc2)

      val doc3 = new SolrInputDocument()
      doc3.addField("id", "3")
      doc3.addField("date", "2017-01-03T00:00:00Z")
      server.add(doc3)

      server.commit()

      val q = new ModifiableSolrParams()
      q.add("q", "*:*")
      q.add("facet", "true")
      q.add("facet.field", "date")
      q.add("facet.range", "date")
      q.add("facet.range.start", "NOW/DAY-30DAYS")
      q.add("facet.range.end", "NOW/DAY+30DAYS")
      q.add("facet.range.gap", "+1DAY")
      val res = server.query(q).getFacetRanges
      val rangeFacet: RangeFacet[Date, Date] = res.get(0).asInstanceOf[RangeFacet[Date, Date]]
      val counts = rangeFacet.getCounts
      for (i <- 0 until counts.size())
        if (counts.get(i).getCount > 0) {
          println("")
        }

    } catch {
      case e: Exception => println(e)
    }
    finally {
      server.shutdown()
    }
    return
  }

}
