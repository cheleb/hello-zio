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

package zionomicon.chap12

import zio._
import zio.Console._
import zio.ZIOAppDefault

object QueueDemo extends ZIOAppDefault {

  private val program = for {
    queue <- Queue.unbounded[Int]
    _     <- queue.take.flatMap(i => printLine(s"Got $i")).forever.fork
    _     <- queue.offer(1)
    _     <- queue.offer(2)
    _     <- queue.offer(3)
    _     <- printLine("Goobbye crual world.")
  } yield ()

  override def run: ZIO[Environment with ZEnv with ZIOAppArgs, Any, Any] =
    program.exitCode

}
