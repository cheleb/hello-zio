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

package hellozio

import zio._
import zio.Console._
import java.net.InetAddress
import java.net.Socket

object HappyTestSocket extends ZIOAppDefault {

  override def run =
    ZIO
      .attemptBlocking(InetAddress.getAllByName("debian.org").toList)
      .map { addresses =>
        addresses.map { address =>
          ZIO.attemptBlocking(new Socket(address, 443)).tap(a => printLine(s"$a"))
        }
      }
      .flatMap(tasks => ReleasableHappyEyeballs(tasks, 2.seconds, closeSocket))
      .tap(o => printLine("Connect: " + o.getRemoteSocketAddress()))
      .exitCode

  def closeSocket(socket: Socket) = ZIO.attemptBlocking(socket.close()).catchAll(_ => ZIO.unit)
}
