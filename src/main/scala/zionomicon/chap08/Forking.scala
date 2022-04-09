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

package zionomicon.chap08

import zio._

object Forking extends ZIOAppDefault {

  val grandChild: UIO[Unit] = ZIO.succeed(println("Hello, World!"))
  val child                 = grandChild.fork.flatMap(fiber => fiber.join)
  val program               = child.fork *> ZIO.never

  override def run: URIO[Any, ExitCode] = program.exitCode

}

object ForkingTwice extends ZIOAppDefault {

  val effect = for {
    _ <- ZIO.succeed(println("Heart beat")).delay(1 second).forever.fork
    _ <- ZIO.succeed(println("Hard work..."))
  } yield ()

  val program = for {
    fiber <- effect.fork
    _     <- ZIO.succeed(println("Doing ")).delay(5.second)
    _     <- fiber.join
  } yield ()

  override def run = program

}

object ForkingInScopeTwice extends ZIOAppDefault {

  def effect(scope: Scope) =
    for {
      _ <- ZIO.succeed(println("Heart beat")).delay(1 second).forever.forkIn(scope)
      _ <- ZIO.succeed(println("Hard work..."))
    } yield ()

  def module =
    for {
      fiber  <- ZIO.scopeWith(scope => effect(scope).fork)
      _      <- ZIO.succeed(println("Hard another work...")).delay(5.seconds)
      result <- fiber.join

    } yield result

  val program = for {
    fiber <- module.fork
    _     <- ZIO.succeed(println("Running another module entirely ")).delay(10.second)
    _     <- fiber.join
  } yield ()

  override def run = program.exitCode

}
