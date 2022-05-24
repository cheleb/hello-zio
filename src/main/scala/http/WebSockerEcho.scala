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
import zhttp.socket.{ Socket, WebSocketFrame }
import zio._
import zio.stream.ZStream

object WebSocketEcho extends ZIOAppDefault {
  private val socket =
    Socket.collect[WebSocketFrame] {
      case WebSocketFrame.Text("FOO") => ZStream.succeed(WebSocketFrame.text("BAR"))
      case WebSocketFrame.Text("BAR") => ZStream.succeed(WebSocketFrame.text("FOO"))
      case WebSocketFrame.Ping        => ZStream.succeed(WebSocketFrame.pong)
      case WebSocketFrame.Pong        => ZStream.succeed(WebSocketFrame.ping)
      case fr @ WebSocketFrame.Text(_) =>
        ZStream.repeat(fr).schedule(Schedule.spaced(1.second)).take(10)
    }

  private val app =
    Http.collectZIO[Request] {
      case Method.GET -> !! / "greet" / name  => ZIO.succeed(Response.text(s"Greetings {$name}!"))
      case Method.GET -> !! / "subscriptions" => socket.toResponse
    }

  override def run =
    Server.start(8090, app).exitCode
}
