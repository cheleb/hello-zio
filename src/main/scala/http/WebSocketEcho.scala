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
import zhttp.service.ChannelEvent.ChannelRead
import zhttp.service.{ ChannelEvent, Server }
import zhttp.socket.{ WebSocketChannelEvent, WebSocketFrame }
import zio._

object WebSocketEcho extends ZIOAppDefault {
  private val socket =
    Http.collectZIO[WebSocketChannelEvent] {
      case ChannelEvent(ch, ChannelRead(WebSocketFrame.Ping)) =>
        ch.writeAndFlush(WebSocketFrame.Pong)

      case ChannelEvent(ch, ChannelRead(WebSocketFrame.Pong)) =>
        ch.writeAndFlush(WebSocketFrame.Ping)

      case ChannelEvent(ch, ChannelRead(WebSocketFrame.Text(text))) =>
        ch.write(WebSocketFrame.text(text)).repeatN(10) *> ch.flush
    }

  private val app =
    Http.collectZIO[Request] {
      case Method.GET -> !! / "greet" / name  => ZIO.succeed(Response.text(s"Greetings {$name}!"))
      case Method.GET -> !! / "subscriptions" => socket.toSocketApp.toResponse
    }

  override def run = Server.start(8091, app)
}
