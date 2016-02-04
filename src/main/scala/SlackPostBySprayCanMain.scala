import akka.actor.{ActorRef, ActorSystem}
import akka.io.IO
import akka.pattern._
import akka.util.Timeout
import spray.can.Http
import spray.can.Http.HostConnectorInfo
import spray.http.HttpMethods._
import spray.http._
import spray.httpx.marshalling._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

object SlackPostBySprayCanMain extends App {

  implicit val system = ActorSystem("my-system")

  import system.dispatcher

  implicit val timeout = Timeout(10 seconds)

  val hostConnectorF: Future[ActorRef] = IO(Http).ask(Http.HostConnectorSetup("slack.com", port = 443, sslEncryption = true)).map {
    case HostConnectorInfo(hostConnector, hostConnectorSetup) => hostConnector
  }


  // 1. Get the your private token.
  // https://api.slack.com/web
  // Push [Create token]
  // 2. Setup following values.
  val formData = FormData(Map(
    "token" -> "", // TODO e.g. xoxp-XXXXXXXXXXXXXX...
    "channel" -> "#random", // TODO e.g. #random
    "text" -> "BOT投稿テスト"
  ))

  val httpReq = HttpRequest(
    method = POST,
    uri = "/api/chat.postMessage",
    entity = marshal(formData).right.get
  )

  val httpResF: Future[HttpResponse] = hostConnectorF.flatMap(_.ask(httpReq).mapTo[HttpResponse])

  val body = Await.result(httpResF, 10 seconds).entity.data.asString
  println(body)

  system.shutdown()
}
