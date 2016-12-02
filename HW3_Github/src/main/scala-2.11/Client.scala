/**
  * Created by Mike on 11/1/2016.
  */

import java.io.File

import akka.actor.Actor.Receive
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph
import akka.actor.{Actor, ActorSystem, Props}
import akka.util.ByteString
import com.scitools.understand._

import scala.concurrent.{Await, Future}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model._
import akka.http.scaladsl.Http

import scala.concurrent.duration._
import play.api.libs.json.{JsNull, JsString, JsValue, Json}
import org.junit.Assert.assertEquals
import scala.io.StdIn
import scala.sys.process.Process

case object download
case object http
case object makeUdbs
case object evalUdbs











object times {
  var go: Int = 0
}

class cloner(url:String,dir:String) extends Actor
{
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  @Override
  def receive = {
    case download =>

      //println("Enter the directory the projects will be placed: ")
      //val dir = StdIn.readLine()

      try
      {
        val output = Process("git clone " + url, new File(dir)).!!
        println("----------------->cloning done")
        times.go += 1

        if(times.go == 2)
          println("End of part 1...stop program and uncomment part 2 as well as comment out part 1")
      }
      catch
      {
        case e: RuntimeException =>
          println("error caught, continuing.....")
      }


  }

}

class udbMaker() extends Actor
{
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  @Override
  def receive = {

    case makeUdbs =>

      println("Enter the directory the projects are in to make databases: (Please add \\ at the end of the directory path) ")
      val dir = StdIn.readLine()
      val folder =new File(dir)
      val listofFiles= folder.listFiles()
      var num:Int = 0

      //println("Files:")
      for(x<-listofFiles){
       //println("list------------->" + x.getName())
      }

      //val pathName = "C:\\Users\\Mike\\Desktop\\git_projects_v1"
      var dirName: String = ""
      for(x <- listofFiles)
      {
        if(!x.isFile()) {
          dirName = x.getName()
          println("-----name---->" + x.getName())

          val udbFile = new File(dir.concat(dirName))

        //  if (!udbFile.exists()) {
            val result = Process(s"und -db $dirName create -languages java add "+dir+x.getName()+"  analyze", new File(dir)).!!
            println("--------------database created-------------------------")
          //}
         // println("---------------database already exists--------------------")

        }

      }
      println("End of part 2...stop program and uncomment part 3 as well as comment out part 2")
//      val udbEval = system.actorOf(Props(new udbEvaluator(dir)))
//      udbEval ! evalUdbs


  }
}


class udbEvaluator() extends Actor
{
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  @Override
  def receive = {

    case evalUdbs =>

      println("Enter the directory the projects are in to compare databases: ")
      val dir = StdIn.readLine()
      val dirName = new File(dir)
      val listofFiles = dirName.listFiles()
      val filenames: Array[String] = new Array[String](3)
      var num:Int = 0

      for (x <- listofFiles) {
        if(x.isFile()) {
          //println("name-------------------->" + x)
          filenames(num) = x.toString()
          //println("name after insert---------->" + filenames(num))
          num += 1
        }
      }
      val database_v1 = filenames(0).toString()
      val database_v2 = filenames(1).toString()

      val version1 = DataFile.openDatabase(database_v1)
      val version2 = DataFile.openDatabase(database_v2)
      println("-------databases opened-------------")

      // graph variables for dependency graphs
      var dependencyGraph_v1: SimpleDirectedGraph[String, DefaultEdge] = new SimpleDirectedGraph[String, DefaultEdge](classOf[DefaultEdge])
      var dependencyGraph_v2: SimpleDirectedGraph[String, DefaultEdge] = new SimpleDirectedGraph[String, DefaultEdge](classOf[DefaultEdge])

      // create dependency graphs for each version
      System.out.println("Creating dependency graph for version1....\n");
      dependencyGraph_v1 = Graph.createDependencyGraph(version1)
      System.out.println("Creating dependency graph for version2....\n");
      dependencyGraph_v2 = Graph.createDependencyGraph(version2)

      // Find the differences between version1 and version2
      System.out.println("")
      Graph.checkIsomorphism(dependencyGraph_v1, dependencyGraph_v2)

  }
}


class completeRequest() extends Actor
{
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  @Override
  def receive = {

    case http =>

      println("Please enter a computer language of which you want to download projects:  ")
      val lang = StdIn.readLine()
      println("Please enter the directory to be used for all purposes: ")
      val dir = StdIn.readLine()
      //println("")

      //send http request
      val responseFuture: Future[HttpResponse] = Http().singleRequest(
        HttpRequest(uri = "https://api.github.com/search/repositories?q=language:"+ lang +"&sort=stars&order=desc"))

      import system.dispatcher

      // wait for response
      val response = Await.result(responseFuture, 15.seconds)

      //response.entity.dataBytes.runFold(ByteString.empty)(_ ++ _).map(_.utf8String).foreach(println)

      val data = response.entity.dataBytes.runFold(ByteString.empty)(_++_).map(_.utf8String)

      val x = ""
      var go:Int = 0

      //parse json for project url's
      for(x <- data)
      {
        val str = Json.parse(x)

        val address = str \\ "html_url"
        val address2 = str \\ "commits_url"

        val it = Iterator.from(1, 2).takeWhile(_ < address.size).map(address(_))

        var i: Int = 1
        var y: Int = 1
        var s: String = ""
        var n: Int = 0

        while (it.hasNext && i < 4)
        {
          s = it.next().toString()
          //println("url ---> " + s)

          if (i > 1)
          {
            val actor = system.actorOf(Props(new cloner(s,dir)))
            actor ! download
          }
          //n += 1
          i += 1
          go += 1
        }
      }
//      println("End of part 1...stop program and uncomment part 2 as well as comment out part 1")
//      val udbActor = system.actorOf(Props(new udbMaker(dir)))
//      udbActor ! makeUdbs
  }

}

object Client extends App{

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val httpComplete = system.actorOf(Props(new completeRequest()))
  val udbEval = system.actorOf(Props(new udbEvaluator()))
  val udbActor = system.actorOf(Props(new udbMaker()))


  /************************************************************************************************************/

  // USE THESE ONE AT A TIME...COULD NOT GET THEM TO WORK TOGETHER

  //this gets and clones the projects(PART 1)
  httpComplete ! http

  //this creates the databases form the cloned projects (PART 2)
  //udbActor ! makeUdbs

  // this compares the projects (PART 3)
  //udbEval ! evalUdbs

  /***************************************************************************************************************/



      //Http().shutdownAllConnectionPools().onComplete(_ => system.terminate())
}
