import java.util
import java.util.concurrent._
import java.util.concurrent.atomic.AtomicInteger

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
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
  val numQueries = 10000
  val maxAcc = 40000
  val nThreads = 10
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

    var solrDir = ""
    if (a.length > 1) {
      solrDir = a(1)
      println(a(1))
    } else {
      solrDir = ExternalFileField.getClass.getResource("/solr").getPath
      println("solrDir=" + solrDir)
    }
    val container = new CoreContainer(solrDir)
    container.load()
    server = new EmbeddedSolrServer(container, "external-field")

    val z = new AtomicInteger(1)
    val cmd = a(0)
    cmd match {
      case "0" => {
        server.optimize(null, true, true, 5)
      }
      case "1" => {
        // generate index

        val executor = Executors.newFixedThreadPool(nThreads)

        val runnable = new Runnable {
          override def run(): Unit = {
            val start = z.getAndIncrement()
            val size = maxProd / nThreads
            println("start = " + ((start - 1) * size))
            println("finish = " + ((start * size) - 1))
            for (i <- (start - 1) * size to (start * size) - 1) {
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
              if (i % 123 == 0) {
                println("commiting doc at iteration " + i)
                server.commit()
              }
            }

            server.commit()
            server.optimize(null, true, true, 5)
          }
        }

        for (_ <- 0 until nThreads)
          executor.execute(runnable)

        executor.shutdown()


      }
      case "2" => {
        // do query testing

        val stats = new DescriptiveStatistics
        for (_ <- 0 until numQueries) {
          val q = new ModifiableSolrParams()
          val low = rand.nextInt(1000)
          val high = low + rand.nextInt(1000)
          val sb = new StringBuilder
          val sb2 = new StringBuilder
          val accNum = rand.nextInt(99) + 1
          sb2.append("{!frange l=1}account" + accNum)
          sb.append("account:account" + accNum).append(")")
          q.add("q", "{!parent which=scope:product}(quantity:[" + low + " TO " + high + "] AND " + sb.toString())
          q.add("fq", "filter(" + sb2.toString + ")")
          val resp = server.query(q)
          stats.addValue(resp.getQTime)
          println("---------------------------------------------")
          println(q.toQueryString)
          println(resp.getResults.getNumFound)
          println(resp.getQTime)
          println("---------------------------------------------")
        }
        println("mean qtime = " + stats.getMean)
      }
    }

    return
  }

}
