import sbtassembly.Plugin.AssemblyKeys._
import scala.io.Source.fromFile
import java.io.{FileNotFoundException, File}

val configPath = "conf/sshconfig"

name := "simpleapp"

version := "0.1"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "org.apache.hadoop" % "hadoop-client" % "2.4.0" % "provided",
  ("org.apache.spark" %% "spark-core" % "0.9.1" % "provided").
    exclude("org.mortbay.jetty", "servlet-api").
    exclude("commons-beanutils", "commons-beanutils-core").
    exclude("commons-collections", "commons-collections").
    exclude("commons-collections", "commons-collections").
    exclude("com.esotericsoftware.minlog", "minlog")
)

resolvers += "Akka Repository" at "http://repo.akka.io/releases/"

val scpTask = TaskKey[String]("scp", "Copies assembly jar to remote location")

traceLevel in scpTask := -1

scpTask <<= (assembly, streams) map { (asm, s) =>
  if(new java.io.File(configPath).exists){
    val account = fromFile(configPath).mkString
    val local = asm.getPath
    val remote = account + ":" + asm.getName
    s.log.info(s"Copying: $local -> $account:$remote")
    println("To run on the remote server copy, paste, adjust and run this: ")
    println("=============================")
    println("ssh " + account)
    println("=============================")
    println("cd spark")
    println("bin/spark-class org.apache.spark.deploy.yarn.Client \\")
    println("--jar /home/hduser/"+asm.getName+" \\")
    println("--args yarn-standalone \\")
    println("--master-memory 512M \\")
    println("--worker-memory 512M \\")
    println("--num-workers 1 \\")
    println("--worker-cores 1 \\")
    s.log.error("--class org.menthal.CLASSNAME")
    println("=============================")
    Process(s"rsync $local $remote") !! s.log
  }
  else {
    sys.error(s"$configPath not found.\n" +
    s"Please make sure you have password-less access to the hadoop/spark machine.\n" +
    s"Then create $configPath in the project root.\n" +
    "The line should contain nothing but the host (e.g. hduser@hd)\n")
    ""
  }
}

