import akka.actor.ActorSystem
import akka.util.Timeout
import spray.client.pipelining._
import spray.http.HttpMethods._
import spray.http._
import spray.httpx.marshalling._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

object SlackPostBySprayClientMain extends App {

  implicit val system = ActorSystem("my-system")

  import system.dispatcher

  implicit val timeout = Timeout(10 seconds)

  val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

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
    uri = "https://slack.com/api/chat.postMessage",
    entity = marshal(formData).right.get
  )

  val httpResF: Future[HttpResponse] = pipeline(httpReq)

  val body = Await.result(httpResF, 10 seconds).entity.data.asString
  println(body)

  system.shutdown()
}
