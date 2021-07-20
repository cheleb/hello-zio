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

import zio.duration._

object RetryApp extends App {

  val program = ZIO
      .fromTry(throw new RuntimeException("poum"))
      .tapError(e => putStrLn(e.getMessage()))
      .retry(Schedule.exponential(100.milliseconds) && Schedule.recurWhile[Throwable] {
        case e => true
      })
      .timeout(1.seconds) *> putStrLn("...Plaf")

  def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] =
    program.exitCode
}
