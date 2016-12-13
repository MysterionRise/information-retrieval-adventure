package org.mystic

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.core.CoreContainer

object ManagedSchemaTesting {

  def main(args: Array[String]): Unit = {
    var server: SolrClient = null
    try {
      val solrDir = ManagedSchemaTesting.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "managed-schema")
      println(server.ping())
      val doc = new SolrInputDocument()
      doc.addField("brand", "Miami Nights 1984")
      doc.addField("description", "Rising artist in new retro wave genre. Each album is so stunning and melodic.")
      val album = new SolrInputDocument()
      album.addField("name", "Early Summer")
      album.addField("year", 2010)
      doc.addChildDocument(album)
      server.add(doc)
      server.commit()
    } finally {
      server.shutdown()
    }
    return
  }
}
