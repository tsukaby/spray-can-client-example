import akka.actor.{ActorRef, ActorSystem}
import akka.io.IO
import akka.pattern._
import akka.util.Timeout
import spray.can.Http
import spray.can.Http.HostConnectorInfo
import spray.http.HttpMethods._
import spray.http.{HttpRequest, HttpResponse}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

object GetExampleMain extends App {

  implicit val system = ActorSystem("my-system")

  import system.dispatcher

  implicit val timeout = Timeout(10 seconds)

  val hostConnectorF: Future[ActorRef] = IO(Http).ask(Http.HostConnectorSetup("example.com", port = 80)).map {
    case HostConnectorInfo(hostConnector, hostConnectorSetup) => hostConnector
  }

  val httpReq = HttpRequest(method = GET, uri = "/")
  val httpResF: Future[HttpResponse] = hostConnectorF.flatMap(_.ask(httpReq).mapTo[HttpResponse])


  val body = Await.result(httpResF, 10 seconds).entity.data.asString
  println(body)

  system.shutdown()
}
