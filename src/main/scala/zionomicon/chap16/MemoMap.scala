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

package zionomicon.chap16

import zio._
import zio.managed._

trait MemoMap[K, -R, +E, +V] {
  def get(k: K): ZManaged[R, E, V]
}

object MemoMap {
  def make[K, R, E, V](lookup: K => ZManaged[R, E, V]): UIO[MemoMap[K, R, E, V]] =
    Ref.make[Map[K, (ZIO[R, E, V], ZManaged.Finalizer)]](Map.empty).map { ref =>
      new MemoMap[K, R, E, V] {

        override def get(k: K): ZManaged[R, E, V] =
          ZManaged {
            ref.modify { map =>
              map.get(k) match {
                case Some((acquire, release)) => ???
                case None =>
                  for {
                    observers    <- Ref.make(0)
                    promise      <- Promise.make[Any, Any]
                    finalizerRef <- Ref.make[ZManaged.Finalizer](ZManaged.Finalizer.noop)

                    resource = ZIO.uninterruptibleMask { restore =>
                      for {
                        env <- ZIO.environment[(R, ZManaged.ReleaseMap)]
                        // FIXME        (r, outerReleaseMap) = env
                      } yield ???
                    }
                  } yield ???
                  ???
              }
            }
          }
      }
    }
}
