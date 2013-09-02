package org.mystic

import scala.Predef.String
import org.apache.lucene.store.FSDirectory
import java.io.{IOException, File}
import org.apache.lucene.index.{DirectoryReader, IndexReader}
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.document.Document
import scala.util.Random

/**
 * @author kperikov
 */
object Searching {
  // @todo this should be configurable
  private final val SOLR_HOME: String = "/home/kperikov/Downloads/solr-4.4.0"
  private final val PATH_TO_INDEX: String = SOLR_HOME + "/example/solr/collection1/data/index"

  def main(args: Array[String]) {
    try {
      val fsDirectory: FSDirectory = FSDirectory.open(new File(PATH_TO_INDEX))
      val indexReader: IndexReader = DirectoryReader.open(fsDirectory)
      val random: Random = new Random()
      val searcher: IndexSearcher = new IndexSearcher(indexReader)
      System.out.println(indexReader.numDocs)
      val childDocument: Document = indexReader.document(random.nextInt(indexReader.numDocs()))

    }
    catch {
      case e: IOException => {
        System.out.println("Error while opening index" + e.getCause)
      }
    }
  }
}
