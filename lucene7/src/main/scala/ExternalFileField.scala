import java.io.{BufferedOutputStream, FileOutputStream, PrintWriter}
import java.util

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.params.ModifiableSolrParams
import org.apache.solr.core.CoreContainer

import scala.Console._
import scala.util.Random

/**
  * using external file field to filter stuff out
  */
object ExternalFileField {

  var server: SolrClient = null
  val maxProd = 80000
  val rand = new Random()

  def main(a: Array[String]) {
    //    val out = new PrintWriter(new BufferedOutputStream(new FileOutputStream("account.txt")))
    //    for (i <- 1 to maxProd) {
    //      if (rand.nextInt(3) < 1) {
    //        out.println("prod" + i + "=1")
    //      }
    //    }
    //    out.flush()
    //    out.close()

    try {
      val solrDir = ExternalFileField.getClass.getResource("/solr").getPath
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "external-field")

      for (i <- 1 to maxProd) {
        val doc = new SolrInputDocument()
        doc.addField("id", "prod" + i)
        doc.addField("brand", rand.nextString(4))
        val childDocs = new util.ArrayList[SolrInputDocument]()
        for (j <- 1 to Math.round(rand.nextFloat() * maxProd)) {
          val child = new SolrInputDocument()
          child.addField("id", "chd%d_%d".format(i, j))
          child.addField("account", "account" + j)
          child.addField("quantity", rand.nextInt(5000))
          childDocs.add(child)
        }
        doc.addChildDocuments(childDocs)
        server.add(doc)
        if (i % 1000 == 0) {
          println("commiting doc at iteration " + i)
          server.commit()
        }
      }

      server.commit()

      val q = new ModifiableSolrParams()
      q.add("q", "*:*")
      q.add("fq", " {!frange l=1}account2 OR {!frange l=1}account1")
      val resp = server.query(q)
      println("---------------------------------------------")
      println(resp.getResults.getNumFound)
      println("---------------------------------------------")


    } catch {
      case e: Exception => println(e)
    }
    finally {
      server.close()
    }
    return
  }

}
