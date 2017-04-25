import java.io.File
import java.util.Scanner

import org.json4s._
import org.json4s.native.JsonMethods._

object ThesaurusAnalyzer {


  def main(a: Array[String]) {
    val in = new Scanner(new File("lucene6/src/main/resources/synonyms.json"))
    val thesaurus = parse(in.nextLine())
    val synonyms = thesaurus.children(2).children
    synonyms.map(x => {

    })
    println()
    return
  }

}
