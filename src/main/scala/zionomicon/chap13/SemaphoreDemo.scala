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

package zionomicon.chap13

import zio._
import zio.clock._
import zio.console._
import zio.duration._

object SemaphoreDemo extends App {

  def queryDatabase(connections: Ref[Int]): URIO[Console with Clock, Unit] =
    connections
      .updateAndGet(_ + 1)
      .flatMap { n =>
        console.putStrLn(s"Aquiring connection, now $n simultaneous connections") *>
        ZIO.sleep(1.second) *>
        console.putStrLn(s"Closing connection, now ${n - 1} simultaneous connections")
      }
      .orDie *> connections.update(_ - 1)

  val program = for {
    ref       <- Ref.make(0)
    semaphore <- Semaphore.make(4)

    _ <- ZIO.foreachPar_(1 to 10)(_ => semaphore.withPermit(queryDatabase(ref)))
  } yield ()

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = program.exitCode

}
