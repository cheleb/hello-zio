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

import sttp.client3._
import sttp.client3.asynchttpclient.zio._
import sttp.ws.WebSocket
import zio._
import zio.Console

object WebSocketZio extends ZIOAppDefault {
  def useWebSocket(ws: WebSocket[RIO[Console, *]]): RIO[Console, Unit] = {
    def send(i: Int) = ws.sendText(s"Hello $i!")
    val receive = ws
      .receiveText()
      .flatMap { t =>
        Console.printLine(s"RECEIVED: $t")
      }

    send(1) *> receive.forever.ignore
  }

  // create a description of a program, which requires two dependencies in the environment:
  // the SttpClient, and the Console
  val sendAndPrint: RIO[Console with SttpClient, Response[Unit]] =
    sendR(
      basicRequest
        .get(uri"ws://localhost:8090/subscriptions")
        .response(asWebSocketAlways(useWebSocket))
    )

  override def run =
    // provide an implementation for the SttpClient dependency; other dependencies are
    // provided by Zio
    sendAndPrint
      .provide(AsyncHttpClientZioBackend.layer(), Console.live)

}
