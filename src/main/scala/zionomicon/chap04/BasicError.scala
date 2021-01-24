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

object BasicError extends App {

  private val program = ZIO.effect(1 / 0).sandbox

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = program.exitCode

}

object CatchAllCauseExercice extends App {

  def failWithMessage(string: String) = ZIO.succeed(throw new Error(string)).sandbox

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = failWithMessage("ouille").exitCode

  def recoverFromSomeDefects[R, E, A](zio: ZIO[R, E, A])(f: Throwable => Option[A]): ZIO[R, E, A] =
    zio.foldCauseM(
      ce =>
        ce.defects.flatMap(f) match {
          case Nil =>
            ce.failureOrCause match {
              case Left(value)  => ZIO.fail(value)
              case Right(value) => ZIO.halt(ce)
            }
          case head :: _ => ZIO.succeed(head)
        },
      a => ZIO.succeed(a)
    )

  def logFailures[R, E, A](zio: ZIO[R, E, A]): ZIO[R, E, A] =
    zio
      .foldCauseM(
        ce =>
          (ce.failureOrCause match {
            case Left(value)  => ZIO.fail(value)
            case Right(value) => ZIO.halt(value)
          }).tapCause(c => ZIO.effectTotal(println(c.prettyPrint))),
        a => ZIO.succeed(a)
      )

  def onAnyFailure[R, E, A](zio: ZIO[R, E, A], handler: ZIO[R, E, Any]): ZIO[R, E, A] =
    zio.foldCauseM(r => handler *> ZIO.halt(r), a => ZIO.succeed(a))

  def ioException[R, A](zio: ZIO[R, Throwable, A]): ZIO[R, java.io.IOException, A] =
    zio.refineOrDie {
      case io: IOException => io
    }

  // #6
  val parseNumber: ZIO[Any, Throwable, Int] =
    ZIO.effect("foo".toInt).refineToOrDie[NumberFormatException]

  // #7
  def left[R, E, A, B](zio: ZIO[R, E, Either[A, B]]): ZIO[R, Either[E, B], A] =
    zio.foldM(
      e => ZIO.fail(Left(e)),
      {
        case Right(value) => ZIO.fail(Right(value))
        case Left(v)      => ZIO.succeed(v)
      }
    )

  def unleft[R, E, A, B](zio: ZIO[R, Either[E, B], A]): ZIO[R, E, Either[A, B]] =
    zio.foldM(
      {
        case Left(e)  => ZIO.fail(e)
        case Right(b) => ZIO.succeed(Right(b))
      },
      a => ZIO.succeed(Left(a))
    )

  // #8
  def right[R, E, A, B](zio: ZIO[R, E, Either[A, B]]): ZIO[R, Either[E, A], B] =
    zio.foldM(
      e => ZIO.fail(Left(e)),
      {
        case Left(a)  => ZIO.fail(Right(a))
        case Right(b) => ZIO.succeed(b)
      }
    )

  def unright[R, E, A, B](zio: ZIO[R, Either[E, A], B]): ZIO[R, E, Either[A, B]] =
    zio.foldM(
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
  ): ZIO[R, E2, A] = zio.sandbox.foldM(handler, a => ZIO.succeed(a))

  // #10
  def catchAllCause2[R, E1, E2, A](
      zio: ZIO[R, E1, A],
      handler: Cause[E1] => ZIO[R, E2, A]
  ): ZIO[R, E2, A] = zio.foldCauseM(handler, a => ZIO.succeed(a))
}
