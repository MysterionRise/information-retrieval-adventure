package org.mystic

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.common.{SolrDocumentList, SolrInputDocument}
import org.apache.solr.core.CoreContainer

import scala.Console._

/**
 *
 */
object TypeAhead {

  def main(a: Array[String]) {
    var server: SolrClient = null
    try {
      val solrDir = TypeAhead.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "type-ahead")
      println(server.ping())

    }
    finally {
      server.shutdown()
    }
    return
  }

}
