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

import zhttp.service.{ ChannelFactory, EventLoopGroup }
import zhttp.socket.{ Socket, WebSocketFrame }
import zio._
import zio.stream.ZStream

object WebSocketSimpleClient extends ZIOAppDefault {

  // Setup client envs
  val env = Scope.default ++ EventLoopGroup.auto() ++ ChannelFactory.auto

  val url = "ws://localhost:8091/subscriptions"

  val app = Socket
    .collect[WebSocketFrame] {
      case WebSocketFrame.Text("BAZ") =>
        Console.printLine("ooo")
        ZStream.succeed(WebSocketFrame.close(1000))
      case frame =>
        Console.printLine("ooo")
        ZStream.succeed(frame)
    }
    .toSocketApp
    .connect(url)

  override def run =
    app.exitCode.provide(env)
}
