package org.menthal

import org.apache.avro.specific.SpecificRecord
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.SparkContext._
import org.menthal.model.events._
import org.menthal.model.scalaevents.adapters.PostgresDump
import org.menthal.model.serialization.ParquetIO
import org.menthal.model.events.Implicits._
import org.menthal.model.graph.Graph


import scala.reflect.ClassTag

object MakeGraphs {

  def main(args:Array[String]) {
    if (args.length < 2) {
      System.err.println("Usage: NewAggregations inFile outFile")
      System.exit(1)
    }
    val sc = new SparkContext(args(0), "UserGraphGeneration", System.getenv("SPARK_HOME"))//, SparkContext.jarOfClass(this.getClass))
    val dumpFile = args(1)
    val outFile = args(2)
    val appSessions = ParquetIO.read[AppSession](dumpFile, sc, None)
    val phoneSessions = appSessions
    val result = phoneSessions
      .map(x => (x._1, sessionToGraph(x._2)))
      .reduceByKey(_ union _)
    ParquetIO.write[(Long, Graph)](sc, result, outFile)
    sc.stop()
  }

  def sessionToGraph(container:AppSessionContainer): Graph = {
    var graph = new Graph()

    val sortedAndGrouped = containers.sortByKey().map{case ((time,user), container) => (user,container)}
    sortedAndGrouped.reduceByKey( _ + _ )

  }

}
