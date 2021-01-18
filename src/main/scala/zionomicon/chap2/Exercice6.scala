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

package zionomicon.chap2

object Exercice6 {
  case class ZIO[-R, +E, +A](run: R => Either[E, A])

  def zipWith[R, E, A, B, C](self: ZIO[R, E, A], that: ZIO[R, E, B], f: (A, B) => C): ZIO[R, E, C] =
    ZIO { r =>
      for {
        a <- self.run(r)
        b <- that.run(r)
      } yield f(a, b)
    }

  def collectAll[R, E, A](
      in: Iterable[ZIO[R, E, A]]
  ): ZIO[R, E, List[A]] =
    ZIO { r =>
      in match {
        case Nil => Right(List.empty)
        case head :: next =>
          for {
            a    <- head.run(r)
            list <- collectAll(next).run(r)
          } yield a :: list
      }
    }

  def foreach[R, E, A, B](in: Iterable[A])(f: A => ZIO[R, E, B]): ZIO[R, E, List[B]] =
    ZIO { r =>
      collectAll(in.map(a => ZIO(r => f(a).run(r)))).run(r)
    }

  def orElse[R, E1, E2, A](self: ZIO[R, E1, A], that: ZIO[R, E2, A]): ZIO[R, E2, A] =
    ZIO(r =>
      self
        .run(r) match {
        case Left(value)  => that.run(r)
        case Right(value) => Right[E2, A](value)
      }
    )

}
