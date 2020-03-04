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

import zio.duration.Duration
import zio.clock.Clock
import zio.Queue
import zio.blocking._
import java.net.InetAddress
import java.net.Socket

object HappyEye {

  def happyEyeball[R, T](
      tasks: List[ZIO[R, Throwable, T]],
      delay: Duration
  ): ZIO[R with Clock, Throwable, T] =
    tasks match {
      case Nil         => ZIO.fail(new IllegalStateException("No more chances."))
      case task :: Nil => task
      case task :: others =>
        Queue.bounded[Unit](1).flatMap { taskFailed =>
          val taskWithSignalOnFailed = task.onError(_ => taskFailed.offer(()))

          val sleepOrFailed = ZIO.sleep(delay).race(taskFailed.take)

          taskWithSignalOnFailed.race(sleepOrFailed *> happyEyeball(others, delay))

        }
    }

  def lookups = effectBlocking(InetAddress.getAllByName("debian.org")).map { addresses =>
    addresses.map { address =>
      effectBlocking(new Socket(address, 443))
    }
  }

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  def happyEyeballRelease[R, T](
      tasks: List[ZIO[R, Throwable, T]],
      delay: Duration,
      closing: T => ZIO[R, Nothing, Unit]
  ): ZIO[R with Clock, Throwable, T] =
    for {
      successful <- Queue.bounded[T](tasks.size)
      enqueueing = tasks.map { xs =>
        xs.map { r =>
          successful.offer(r)
        }
      }
      _      <- happyEyeball(enqueueing, delay)
      first  <- successful.take
      others <- successful.takeAll
      _      <- ZIO.foreach(others)(closing)

    } yield first

}
