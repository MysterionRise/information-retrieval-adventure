import java.io.File
import java.util.Scanner

import org.json4s._
import org.json4s.native.JsonMethods._

import scala.collection.mutable


object ThesaurusAnalyzer {


  def main(a: Array[String]) {
    val in = new Scanner(new File("lucene6/src/main/resources/synonyms.json"))
    val thesaurus = parse(in.nextLine())
    val synonyms = thesaurus.children(2).children
    val phrases = new mutable.HashSet[String]
    for (i <- 0 until synonyms.size) {
      synonyms(i).children.size match {
        //multi-way
        case 2 => {
          synonyms(i).children.apply(0).children.foreach(value => {
            phrases.add(value.asInstanceOf[JString].s.trim)
          })
        }
        //one way
        case 3 => {
          phrases.add(synonyms(i).children.apply(0).asInstanceOf[JString].s.trim)
        }
        case _ => {
          println(synonyms(i).children)
        }
      }
    }
    val sorted = phrases.toList.sorted.toArray.filter(_.contains(" "))
    val length = sorted.length
    for (i <- 0 until length) {
      for (j <- 0 until length) {
        if (i != j && sorted(j).startsWith(sorted(i).split(" ").apply(sorted(i).split(" ").length - 1))) {
          println(s"${sorted(i)} | ${sorted(j)}")
        }
      }
    }
    //
    for (i <- 0 until length) {
      for (j <- 0 until length) {
        for (k <- 0 until length) {
          if (i != k && i != j && j != k && sorted(k).startsWith(sorted(i)) && sorted(k).endsWith(sorted(j))) {
            println(s"${sorted(i)} | ${sorted(k)} | ${sorted(j)}")
          }
        }
      }
    }
    return
  }

}
