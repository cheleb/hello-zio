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

import java.io.PrintWriter

object Simple extends ZIOAppDefault {

  def zio1  = ZIO.succeed(Some(false))
  def zio2  = ZIO.succeed(Some(true))
  def boom  = ZIO.fail("Aille").sandbox
  def dummy = ZIO.succeed(1)

  val z = zio1 *> zio2

  val program = for {
    res <- zio1.zip(zio2) map {
      case (Some(a), Some(b)) if a && b => None
      case _                            => Some(true)
    }
    _ <- printLine(s" $res one two")

    _ <-
      ZManaged
        .fromAutoCloseable(ZIO.attemptBlocking(new PrintWriter("/tmp/zozo")))
        .use(w => ZIO.attemptBlocking(w.append("zozo")))
        .ignore

  } yield ()

  override def run = program

}
