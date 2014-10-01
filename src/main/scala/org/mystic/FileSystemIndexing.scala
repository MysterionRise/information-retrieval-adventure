package org.mystic

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.core.CoreContainer

object FileSystemIndexing {

  def main(args: Array[String]): Unit = {

    // @todo fix absolute path here
    val coreContainer = new CoreContainer("/home/kperikov/projects/octo-solr-adventure/src/main/resources/solr_home")
    println(FileSystemIndexing.getClass.getResource("/solr_home").toString)
    coreContainer.load()
    val solrServer = new EmbeddedSolrServer(coreContainer, "file-indexing-test")
    println(solrServer.ping())

  }
}
