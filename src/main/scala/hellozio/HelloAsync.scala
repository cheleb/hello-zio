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

object HelloAsync extends App {

  def getUserByIdAsync(id: Int)(cb: Option[String] => Unit): Unit =
    new Thread {
      override def run(): Unit =
        id match {
          case 1 => cb(Some("Olivier"))
          case _ => cb(None)
        }
    }.start()

  def getUserById(id: Int): ZIO[Any, None.type, String] =
    ZIO.effectAsync { callback =>
      getUserByIdAsync(id) {
        case Some(name) => callback(ZIO.succeed(name))
        case None       => callback(ZIO.fail(None))
      }
    }

  private val program = for {
    _    <- console.putStrLn("Hello async")
    user <- getUserById(1)
    _    <- console.putStrLn(s"Hi $user")
  } yield ()

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = program.exitCode

}
