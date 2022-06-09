/*
 * Copyright 2020 Olivier NOUGUIER
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package http

import zio._
import zhttp.http._
import zhttp.service.Server
import zhttp.socket.{ Socket, WebSocketFrame }
import zio.stream.ZStream
import sttp.client3.{ Response => SttpResponse, UriContext, asWebSocketAlways, basicRequest }

import sttp.client3.asynchttpclient.zio._
import sttp.ws.WebSocket
import zio.Console

object WebSocketBridge extends ZIOAppDefault {
  def useWebSocket(txt: String, queue: Queue[WebSocketFrame])(
      ws: WebSocket[RIO[Console, *]]
  ): RIO[Console, Unit] = {
    def send(txt: String) = ws.sendText(txt)
    val receive = ws
      .receiveText()
      .flatMap { t =>
        Console.printLine(s"----> $t ---->") <* queue.offer(WebSocketFrame.text(t))
      }

    ZIO.debug("Degin") *> send(txt) *> receive.forever.ignore *> ZIO.debug("end") <* queue
      .offer(
        WebSocketFrame.close(1000, None)
      )
  }

  // create a description of a program, which requires two dependencies in the environment:
  // the SttpClient, and the Console
  def forwardAndPrint(
      txt: String,
      queue: Queue[WebSocketFrame]
  ): RIO[Console with SttpClient, SttpResponse[Unit]] =
    sendR(
      basicRequest
        .get(uri"ws://localhost:8091/subscriptions")
        .response(asWebSocketAlways(useWebSocket(txt, queue)))
    )

  private def socket() =
    Socket.collect[WebSocketFrame] {
      case WebSocketFrame.Ping => ZStream.succeed(WebSocketFrame.pong)
      case WebSocketFrame.Pong => ZStream.succeed(WebSocketFrame.ping)
      case fr @ WebSocketFrame.Text(txt) =>
        val z = for {
          queue <- ZStream.fromZIO(Queue.unbounded[WebSocketFrame])
          _     <- ZStream.fromZIO(forwardAndPrint(txt, queue))
          zz    <- ZStream.fromQueue(queue)
        } yield zz

        z ++ ZStream.succeed(WebSocketFrame.close(1000, None))
    }

  private val app =
    Http.collectZIO[Request] {
      case Method.GET -> !! / "greet" / name => ZIO.succeed(Response.text(s"Greetings {$name}!"))
      case Method.GET -> !! / "subscriptions" =>
        socket().toResponse
    }

  override def run =
    Server.start(8090, app).provide(AsyncHttpClientZioBackend.layer(), Console.live)
}
