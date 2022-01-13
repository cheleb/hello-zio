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

import zio.ZIO

import zio._
object HappyEyeballs {

  def apply[R, T](
      tasks: List[ZIO[R, Throwable, T]],
      delay: Duration
  ): ZIO[R with Clock, Throwable, T] =
    tasks match {
      case Nil         => ZIO.fail(new IllegalArgumentException("No tasks"))
      case task :: Nil => task
      case task :: otherTasks =>
        Queue.bounded[Unit](1).flatMap { taskFailed =>
          val taskWithFailedSignal = task.onError(_ => taskFailed.offer(()))
          val sleepOrFailed        = ZIO.sleep(delay).race(taskFailed.take)

          taskWithFailedSignal.race(sleepOrFailed *> apply(otherTasks, delay))
        }
    }

}
