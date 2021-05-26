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

import zio.console._

object Simple extends App {

  def zio1  = ZIO.succeed(Some(false))
  def zio2  = ZIO.succeed(Some(true))
  def boom  = ZIO.fail("Aille").sandbox
  def dummy = ZIO(1)

  val z = zio1 *> zio2

  val program = for {
    res <- zio1.zip(zio2) map {
      case (Some(a), Some(b)) if a && b => None
      case _                            => Some(true)
    }
    _ <- putStrLn(s" $res one two")
  } yield ()

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = program.exitCode

}
