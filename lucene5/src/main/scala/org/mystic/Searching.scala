package org.mystic

import org.apache.lucene.index.{DirectoryReader, IndexReader}
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.BytesRef

import java.io.IOException
import java.nio.file.Paths
import scala.Console._

object Searching {
  // @todo this should be configurable
  private final val SOLR_HOME: String = "/home/kperikov/Downloads/solr-4.4.0"
  private final val PATH_TO_INDEX: String = SOLR_HOME + "/example/solr/collection1/data/index"

  def main(args: Array[String]): Unit = {
    try {
      val fsDirectory: FSDirectory = FSDirectory.open(Paths.get(PATH_TO_INDEX))
      val indexReader: IndexReader = DirectoryReader.open(fsDirectory)
      val contexts = indexReader.leaves
      val it = contexts.listIterator
      var i = 0
      while (it.hasNext) {
        val arc = it.next
        println("Index of segment is " + i)
        i += 1
        val fieldsIT = arc.reader().fields().iterator()
        val f = arc.reader().fields()
        while (fieldsIT.hasNext) {
          val field = fieldsIT.next()
          println(f.terms(field).size())
          val docValues = arc.reader().getSortedDocValues(field)
          if (docValues != null) {
            var ref: BytesRef = new BytesRef()
            ref = docValues.lookupOrd(docValues.getOrd(1))
            println(ref.utf8ToString() + " ")
            ref = docValues.lookupOrd(docValues.getOrd(100))
            println(ref.utf8ToString() + " ")
            ref = docValues.lookupOrd(docValues.getOrd(10000))
            println(ref.utf8ToString() + " ")
          }
        }
      }
    }
    catch {
      case e: IOException =>
        println("Error while opening index" + e.getCause)
    }
  }
}
