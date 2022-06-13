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

import zhttp.http._
import zhttp.service.Server
import zhttp.socket._
import zio._

import zio.stream.ZStream

object WebSocketAdvanced extends ZIOAppDefault {
  // Message Handlers
  private val open = Socket.succeed(WebSocketFrame.text("Greetings!"))

  private val echo = Socket.collect[WebSocketFrame] { case WebSocketFrame.Text(text) =>
    ZStream
      .range(0, text.toIntOption.getOrElse(1))
      .map(i => s"Echo ((( $i )))")
      .tap(message => ZIO.debug(message))
      .map(message => WebSocketFrame.text(message))
      .schedule(Schedule.spaced(1 second)) ++ ZStream.succeed(WebSocketFrame.close(1000, None))
  }

  private val fooBar = Socket.collect[WebSocketFrame] {
    case WebSocketFrame.Text("FOO") => ZStream.succeed(WebSocketFrame.text("BAR"))
    case WebSocketFrame.Text("BAR") => ZStream.succeed(WebSocketFrame.text("FOO"))
  }

  // Setup protocol settings
  private val protocol = SocketProtocol.subProtocol("json")

  // Setup decoder settings
  private val decoder = SocketDecoder.allowExtensions

  // Combine all channel handlers together
  private val socketApp =
    SocketApp(echo merge fooBar) // Called after each message being received on the channel

      // Called after the request is successfully upgraded to websocket
      .onOpen(open)

      // Called after the connection is closed
      .onClose(_ => Console.printLine("Closed!").ignore)

      // Called whenever there is an error on the socket channel
      .onError(_ => Console.printLine("Error!").ignore)

      // Setup websocket decoder config
      .withDecoder(decoder)

      // Setup websocket protocol config
      .withProtocol(protocol)

  private val app =
    Http.collectZIO[Request] {
      case Method.GET -> !! / "greet" / name  => ZIO.attempt(Response.text(s"Greetings ${name}!"))
      case Method.GET -> !! / "subscriptions" => ZIO.debug("begin") *> socketApp.toResponse
    }

  override def run = Server.start(8091, app)
}
