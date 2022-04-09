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
import zio.managed._
import zio.Console._
import zio.ZIOAppDefault

object ZManagedTest extends ZIOAppDefault {

  val managed = for {
    za <-
      ZManaged
        .fromAutoCloseable(ZIO.succeed(new AutoCloseable {

          override def close(): Unit = println("ooo 1")

        }))
        .map(_ => 1)
    zb <-
      ZManaged
        .fromAutoCloseable(ZIO.succeed(new AutoCloseable {

          override def close(): Unit = println("ooo 2")

        }))
        .map(_ => 2)
  } yield za + zb

  val program = for {
    _ <- printLine("Start")
    _ <- managed.use(res => printLine(s"res: $res"))
    _ <- printLine("End")
  } yield ()

  override def run =
    program

}
