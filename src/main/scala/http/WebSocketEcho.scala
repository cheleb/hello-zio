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
      case WebSocketFrame.Ping => ZStream.succeed(WebSocketFrame.pong)
      case WebSocketFrame.Pong => ZStream.succeed(WebSocketFrame.ping)
      case fr @ WebSocketFrame.Text(txt) =>
        ZStream
          .range(1, 5)
          .tap(i => ZIO.sleep(i.second))
          .map(i => WebSocketFrame.Text(s"Echo ((( $i )))"))
          .schedule(Schedule.spaced(1.second)) ++ ZStream.succeed(WebSocketFrame.close(1000, None))
    }

  private val app =
    Http.collectZIO[Request] {
      case Method.GET -> !! / "greet" / name  => ZIO.succeed(Response.text(s"Greetings {$name}!"))
      case Method.GET -> !! / "subscriptions" => socket.toResponse
    }

  override def run = Server.start(8091, app)
}