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

object ZManagedTest extends App {

  val managed = for {
    za <-
      ZManaged
        .fromAutoCloseable(ZIO(new AutoCloseable {

          override def close(): Unit = println("ooo 1")

        }))
        .map(_ => 1)
    zb <-
      ZManaged
        .fromAutoCloseable(ZIO(new AutoCloseable {

          override def close(): Unit = println("ooo 2")

        }))
        .map(_ => 2)
  } yield za + zb

  val program = for {
    _ <- putStrLn("Start")
    _ <- managed.use(res => putStrLn(s"res: $res"))
    _ <- putStrLn("End")
  } yield ()

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    program.exitCode

}
