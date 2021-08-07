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

package zionomicon.chap07

import zio._
import zio.Console._

object Concur extends App {

  private val zio1 = ZIO.sleep(100 millis) *> ZIO(1)
  private val zio2 = ZIO.sleep(500 millis) *> ZIO(2)

  private val program = for {
    res <- zio1.raceEither(zio2)
    _   <- putStrLn(s"Res: $res")
  } yield ()

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = program.exitCode

}

object ValidatePar extends App {

  private def zioOf(i: Int) =
    ZIO.sleep(100 * i millis) *> putStrLn(s"$i") *> (if (i % 3 == 0) ZIO.fail(i)
                                                     else ZIO(i).orElseFail(0))

  private val program = for {
    res <- ZIO.validatePar(1 to 10)(zioOf)
    _   <- putStrLn(s"Res: $res")
  } yield ()

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    program.catchAll {
      case e => putStrLnErr(e.toString())
    }.exitCode

}
