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

object WebSocketZio extends ZIOAppDefault {
  def useWebSocket(n: Int)(ws: WebSocket[RIO[Any, *]]): RIO[Any, Unit] = {
    def send(i: Int) = ws.sendText(s"$i")
    val receive = ws
      .receiveText()
      .flatMap(ZIO.debug(_))

    send(n) *> receive.forever.ignore
  }

  // create a description of a program, which requires two dependencies in the environment:
  // the SttpClient, and the Console
  def sendAndPrint(n: Int = 1): RIO[SttpClient, Response[Unit]] =
    sendR(
      basicRequest
        .get(uri"ws://localhost:8090/subscriptions")
        .response(asWebSocketAlways(useWebSocket(n)))
    )

  override def run =
    for {
      args <- getArgs
      _ <- sendAndPrint(args.headOption.flatMap(_.toIntOption).getOrElse(1))
        .provide(AsyncHttpClientZioBackend.layer())

    } yield ()

}
