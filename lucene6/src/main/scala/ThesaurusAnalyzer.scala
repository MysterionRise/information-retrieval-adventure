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
        case 2 => {
//          synonyms(i).children.apply(0).children.foreach(value => {
//            phrases.add(value.asInstanceOf[JString].s.trim)
//          })
        }
        case 3 => {
//          synonyms(i).children.apply(1).children.foreach(value => {
//            phrases.add(value.asInstanceOf[JString].s.trim)
//          })
//          if (synonyms(i).children.apply(2).asInstanceOf[JString].s.equalsIgnoreCase("multi-way")) {
            phrases.add(synonyms(i).children.apply(0).asInstanceOf[JString].s.trim)
//          }
        }
        case _ => {
          println(synonyms(i).children)
        }
      }
    }
    val sorted = phrases.toList.sorted.toArray
    for (i <- 0 until sorted.length) {
      for (j <- i + 1 until sorted.length) {
        if (sorted(j).length != sorted(i).length && sorted(j).startsWith(sorted(i))) {
          println(s"${sorted(i)} # ${sorted(j)}")
        }
      }
    }
    return
  }

}
