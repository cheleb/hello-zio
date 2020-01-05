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

import zio.App
import zio.ZIO

import zio.console._

object MyApp extends App {

  def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = myAppLogic.fold(_ => 1, _ => 0)

  val myAppLogic = for {
    _    <- putStrLn("Enter your name:")
    name <- getStrLn
    _    <- putStrLn(s"Hello, $name")
  } yield ()

}
