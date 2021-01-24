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

package zionomicon.chap3

import zio._
import zio.console._
object ConsoleApp extends App {

  val greet = for {
    _    <- putStrLn("Who are you?")
    name <- getStrLn
    _    <- putStrLn(s"Hi $name")
  } yield ()

  override def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] = greet.exitCode

}
