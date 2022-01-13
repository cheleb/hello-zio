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

import java.time.Duration
import zio.Exit.Success
import zio.Exit.Failure

object ReleasableHappyEyeballs {
  def apply[R, T](
      tasks: List[ZIO[R, Throwable, T]],
      delay: Duration,
      releaseExtra: T => ZIO[R, Nothing, Unit]
  ): ZIO[R with Clock, Throwable, T] =
    for {
      successful <- Queue.bounded[T](tasks.size)
      enqueingTasks = tasks.map { task =>
        task.onExit {
          case Success(value) => successful.offer(value)
          case Failure(cause) => ZIO.unit
        }
      }
      _     <- HappyEyeballs(enqueingTasks, delay)
      chunk <- successful.takeAll
      first :: others = chunk.toList
      _ <- ZIO.foreach(others)(releaseExtra)
    } yield first
}
