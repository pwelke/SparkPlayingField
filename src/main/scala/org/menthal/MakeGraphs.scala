package org.menthal

import org.apache.avro.specific.SpecificRecord
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.menthal.model.events._
import org.menthal.model.scalaevents.adapters.PostgresDump
import org.menthal.model.serialization.ParquetIO
import org.menthal.model.events.Implicits._
import com.twitter.algebird._
import com.twitter.algebird.Operators._

import scala.reflect.ClassTag

object MakeGraphs {
  def main(args: Array[String]) {
    if (args.length != 3) {
      System.err.println("First argument is master, second input path, third argument is output path")
    }
    else {
      val sc = new SparkContext(args(0),
        "MakeGraphs",
        System.getenv("SPARK_HOME"),
        Nil,
        Map(
          "spark.serializer" -> "org.apache.spark.serializer.KryoSerializer",
          "spark.kryo.registrator" -> "org.menthal.model.serialization.MenthalKryoRegistrator",
          "spark.kryo.referenceTracking" -> "false")
      )
      val inFile = args(1)
      val outputFile = args(2)
      work(sc, inFile, outputFile)
      sc.stop()
    }
  }

  def main(args:Array[String]) {
    if (args.length < 2) {
      System.err.println("Usage: NewAggregations dumpFile")
      System.exit(1)
    }
    val sc = new SparkContext(args(0), "UserGraphGeneration", System.getenv("SPARK_HOME"))//, SparkContext.jarOfClass(this.getClass))
    val dumpFile = args(1)
    val eventsDump = sc.textFile(dumpFile,2)
    val events = NewAggregations.linesToEvents(eventsDump)
    val aggregations = NewAggregations.reduceToAppContainers(events)
    aggregations.map(x => sessionToGraph(x._1, x._2))
    sc.stop()
  }

  def sessionToGraph(uid:Long, container:AppSessionContainer) = {


  }

  class Graph() {
    var vertices = Set[String]()
    var edges = Map[(String, String), Int]()
    var totalWeight = 0

      /**
       * Add a weighted edge between two vertices v, w.
       * If one or both vertices do not exist, add them.
       * If there is already an edge between v and w, increase
       * its weight by one.
       */
    def addEdge(v: String, w: String) {
      vertices += v
      vertices += w
      edges += Map((v, w) -> 1) // map algebra: if (v,w) already exists, 1 will be added to the old value
      totalWeight += 1
    }

    /**
     * Add an edge between vertices that correspond to AppSessionFragments in the following way:
     *
     * @param a
     * @param b
     */
    def addEdge(a: AppSessionFragment, b: AppSessionFragment) {
      val as = a match {
        case u: Unlock => "Unlock"
        case l: Lock => "Lock"
        case s: Session => s.app.getOrElse("UnknownApp")
      }
      val bs = a match {
        case u: Unlock => "Unlock"
        case l: Lock => "Lock"
        case s: Session => s.app.getOrElse("UnknownApp")
      }
      addEdge(as, bs)
    }
  }

}
