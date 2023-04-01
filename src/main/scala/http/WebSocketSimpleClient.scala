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

import zio._
import zio.http.ChannelEvent.{ ChannelRead, UserEvent, UserEventTriggered }
import zio.http.socket.{ WebSocketChannelEvent, WebSocketFrame }
import zio.http.{ ChannelEvent, Client, Http, Response }

object WebSocketSimpleClient extends ZIOAppDefault {

  val url = "ws://ws.vi-server.org/mirror"

  val httpSocket: Http[Any, Throwable, WebSocketChannelEvent, Unit] =
    Http

      // Listen for all websocket channel events
      .collectZIO[WebSocketChannelEvent] {

        // Send a "foo" message to the server once the connection is established
        case ChannelEvent(ch, UserEventTriggered(UserEvent.HandshakeComplete)) =>
          ch.writeAndFlush(WebSocketFrame.text("foo"))

        // Send a "bar" if the server sends a "foo"
        case ChannelEvent(ch, ChannelRead(WebSocketFrame.Text("foo"))) =>
          ch.writeAndFlush(WebSocketFrame.text("bar"))

        // Close the connection if the server sends a "bar"
        case ChannelEvent(ch, ChannelRead(WebSocketFrame.Text("bar"))) =>
          ZIO.succeed(println("Goodbye!")) *> ch.writeAndFlush(WebSocketFrame.close(1000))
      }

  val app: ZIO[Any with Client with Scope, Throwable, Response] =
    httpSocket.toSocketApp.connect(url) *> ZIO.never

  val run = app.provide(Client.default, Scope.default)

}
