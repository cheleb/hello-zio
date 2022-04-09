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

package zionomicon.chap11

import zio._
import zio.ZIOAppDefault

trait Cache[-K, +E, +V] { def get(key: K): IO[E, V] }
object Cache {

  def make[K, R, E, V](lookup: K => ZIO[R, E, V]): URIO[R, Cache[K, E, V]] =
    for {
      r   <- ZIO.environment[R]
      ref <- Ref.make[Map[K, Promise[E, V]]](Map.empty)
    } yield new Cache[K, E, V] {
      def get(key: K): IO[E, V] =
        Promise.make[E, V].flatMap { promise =>
          ref
            .modify { map =>
              map.get(key) match {
                case Some(promise) => (Right(promise), map)
                case None          => (Left(promise), map + (key -> promise))
              }
            }
            .flatMap {
              case Left(promise) =>
                lookup(key).provideEnvironment(r).intoPromise(promise) *> promise.await
              case Right(promise) => promise.await
            }
        }
    }
}

object CacheTest extends ZIOAppDefault {

  override def run =
    Cache.make((str: String) => ZIO.attempt(1)).exitCode

}
