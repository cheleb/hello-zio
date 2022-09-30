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

package zionomicon.chap04

import zio._
import java.io.IOException
import zio.ZIOAppDefault

object BasicError extends ZIOAppDefault {

  private val program = ZIO.attempt(1 / 0).sandbox

  override def run = program.exitCode

}

object CatchAllCauseExercice extends ZIOAppDefault {

  def failWithMessage(string: String) = ZIO.attempt(throw new RuntimeException(string)).ignore

  override def run = failWithMessage("ouille").exitCode

  def recoverFromSomeDefects[R, E, A](zio: ZIO[R, E, A])(f: Throwable => Option[A]): ZIO[R, E, A] =
    zio.foldCauseZIO(
      ce =>
        ce.defects.flatMap(f) match {
          case Nil =>
            ce.failureOrCause match {
              case Left(value)  => ZIO.fail(value)
              case Right(value) => ZIO.failCause(ce)
            }
          case head :: _ => ZIO.succeed(head)
        },
      a => ZIO.succeed(a)
    )

  def logFailures[R, E, A](zio: ZIO[R, E, A]): ZIO[R, E, A] = ???
  def onAnyFailure[R, E, A](zio: ZIO[R, E, A], handler: ZIO[R, E, Any]): ZIO[R, E, A] =
    zio.foldCauseZIO(r => handler *> ZIO.failCause(r), a => ZIO.succeed(a))

  def ioException[R, A](zio: ZIO[R, Throwable, A]): ZIO[R, java.io.IOException, A] =
    zio.refineOrDie { case io: IOException =>
      io
    }

  // #6
  val parseNumber: ZIO[Any, Throwable, Int] =
    ZIO.attempt("foo".toInt).refineToOrDie[NumberFormatException]

  // #7
  def left[R, E, A, B](zio: ZIO[R, E, Either[A, B]]): ZIO[R, Either[E, B], A] =
    zio.foldZIO(
      e => ZIO.fail(Left(e)),
      {
        case Right(value) => ZIO.fail(Right(value))
        case Left(v)      => ZIO.succeed(v)
      }
    )

  def unleft[R, E, A, B](zio: ZIO[R, Either[E, B], A]): ZIO[R, E, Either[A, B]] =
    zio.foldZIO(
      {
        case Left(e)  => ZIO.fail(e)
        case Right(b) => ZIO.succeed(Right(b))
      },
      a => ZIO.succeed(Left(a))
    )

  // #8
  def right[R, E, A, B](zio: ZIO[R, E, Either[A, B]]): ZIO[R, Either[E, A], B] =
    zio.foldZIO(
      e => ZIO.fail(Left(e)),
      {
        case Left(a)  => ZIO.fail(Right(a))
        case Right(b) => ZIO.succeed(b)
      }
    )

  def unright[R, E, A, B](zio: ZIO[R, Either[E, A], B]): ZIO[R, E, Either[A, B]] =
    zio.foldZIO(
      {
        case Left(e)  => ZIO.fail(e)
        case Right(a) => ZIO.succeed(Left(a))
      },
      b => ZIO.succeed(Right(b))
    )
// #9
  def catchAllCause[R, E1, E2, A](
      zio: ZIO[R, E1, A],
      handler: Cause[E1] => ZIO[R, E2, A]
  ): ZIO[R, E2, A] = zio.sandbox.foldZIO(handler, a => ZIO.succeed(a))

  // #10
  def catchAllCause2[R, E1, E2, A](
      zio: ZIO[R, E1, A],
      handler: Cause[E1] => ZIO[R, E2, A]
  ): ZIO[R, E2, A] = zio.foldCauseZIO(handler, a => ZIO.succeed(a))
}
