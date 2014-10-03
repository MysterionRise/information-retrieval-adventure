package org.mystic

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.core.CoreContainer

object FileSystemIndexing {

  def main(args: Array[String]): Unit = {

    val solrDir = FileSystemIndexing.getClass.getResource("/solr").getPath
    val container = new CoreContainer(solrDir)
    container.load()
    val server = new EmbeddedSolrServer(container, "test")
    println(server.ping())
    server.shutdown()
    return
  }
}
