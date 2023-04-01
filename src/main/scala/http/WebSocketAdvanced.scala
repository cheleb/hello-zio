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

package example

import zio._
import zio.http.ChannelEvent.{ ChannelRead, ExceptionCaught, UserEvent, UserEventTriggered }
import zio.http._
import zio.http.model.Method
import zio.http.socket._

object WebSocketAdvanced extends ZIOAppDefault {
  val messageFilter: Http[Any, Nothing, WebSocketChannelEvent, (Channel[WebSocketFrame], String)] =
    Http.collect[WebSocketChannelEvent] {
      case ChannelEvent(channel, ChannelRead(WebSocketFrame.Text(message))) =>
        (channel, message)
    }

  val messageSocket: Http[Any, Throwable, WebSocketChannelEvent, Unit] = messageFilter >>>
    Http.collectZIO[(WebSocketChannel, String)] {
      case (ch, "end") => ch.close()

      // Send a "bar" if the server sends a "foo"
      case (ch, "foo") => ch.writeAndFlush(WebSocketFrame.text("bar"))

      // Send a "foo" if the server sends a "bar"
      case (ch, "bar") => ch.writeAndFlush(WebSocketFrame.text("foo"))

      // Echo the same message 10 times if it's not "foo" or "bar"
      // Improve performance by writing multiple frames at once
      // And flushing it on the channel only once.
      case (ch, text) =>
        ch.write(WebSocketFrame.text(text)).repeatN(10) *> ch.flush
    }

  val channelSocket: Http[Any, Throwable, WebSocketChannelEvent, Unit] =
    Http.collectZIO[WebSocketChannelEvent] {

      // Send a "greeting" message to the server once the connection is established
      case ChannelEvent(ch, UserEventTriggered(UserEvent.HandshakeComplete)) =>
        ch.writeAndFlush(WebSocketFrame.text("Greetings!"))

      // Log when the channel is getting closed
      case ChannelEvent(_, ChannelRead(WebSocketFrame.Close(status, reason))) =>
        Console.printLine("Closing channel with status: " + status + " and reason: " + reason)

      // Print the exception if it's not a normal close
      case ChannelEvent(_, ExceptionCaught(cause)) =>
        Console.printLine(s"Channel error!: ${cause.getMessage}")
    }

  val httpSocket: Http[Any, Throwable, WebSocketChannelEvent, Unit] =
    messageSocket ++ channelSocket

  val protocol = SocketProtocol(Some("json")) // Setup protocol settings

  val decoder = SocketDecoder.default // Setup decoder settings

  val socketApp: SocketApp[Any] = // Combine all channel handlers together
    httpSocket.toSocketApp
      .withDecoder(decoder)   // Setup websocket decoder config
      .withProtocol(protocol) // Setup websocket protocol config

  val app: Http[Any, Nothing, Request, Response] =
    Http.collectZIO[Request] {
      case Method.GET -> !! / "greet" / name  => ZIO.succeed(Response.text(s"Greetings ${name}!"))
      case Method.GET -> !! / "subscriptions" => socketApp.toResponse
    }

  override val run = Server.serve(app).provide(Server.default)
}
