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
  val maxAcc = 40000
  val rand = new Random()

  def main(a: Array[String]) {
    //    for (i <- 1 to 99) {
    //      val out = new PrintWriter(new BufferedOutputStream(new FileOutputStream("external_account" + i)))
    //      for (i <- 1 to maxProd) {
    //        if (rand.nextInt(4) < 1) {
    //          out.println("prod" + i + "=1")
    //        }
    //      }
    //      out.flush()
    //      out.close()
    //    }


    try {
      var solrDir = ""
      if (a.length > 0) {
        solrDir = a(0)
        println(a(0))
      } else {
        solrDir = ExternalFileField.getClass.getResource("/solr").getPath
      }
      val container = new CoreContainer(solrDir)
      container.load()
      server = new EmbeddedSolrServer(container, "external-field")

            for (i <- 1 to maxProd) {
              val doc = new SolrInputDocument()
              doc.addField("id", "prod" + i)
              doc.addField("scope", "product")
              doc.addField("brand", rand.nextString(4))
              val childDocs = new util.ArrayList[SolrInputDocument]()
              for (j <- 1 to Math.round((rand.nextFloat() / 3.0f) * maxAcc)) {
                val child = new SolrInputDocument()
                child.addField("id", "chd%d_%d".format(i, j))
                child.addField("scope", "child")
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
      server.optimize()

      val q = new ModifiableSolrParams()
      for (_ <- 0 until 1000) {
        val ac = rand.nextInt(10)
        val low = rand.nextInt(100)
        val high = low + rand.nextInt(1000)
        val sb = new StringBuilder
        val sb2 = new StringBuilder
        sb.append("(")
        for (j <- 1 to ac) {
          val accNum = rand.nextInt(99) + 1
          sb2.append("{!frange l=1}account" + accNum)
          sb.append("account:account" + accNum)
          if (j != ac) {
            sb.append(" OR ")
            sb2.append(" OR ")
          }
        }
        sb.append("))")
        q.add("q", "{!parent which=scope:product}(quantity:[" + low + " TO " + high + "] AND " + sb.toString())
        q.add("fq", "filter(" + sb2.toString + ")")
        val resp = server.query(q)
        println("---------------------------------------------")
        println(q.toQueryString)
        println(resp.getResults.getNumFound)
        println(resp.getQTime)
        println("---------------------------------------------")
      }


    } catch {
      case e: Exception => println(e)
    }
    finally {
      server.close()
    }
    return
  }

}
