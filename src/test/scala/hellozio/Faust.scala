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

class IO[+A](val unsafeInterpret: () => A) { self =>

  def map[B](f: A => B): IO[B] = flatMap(f.andThen(IO.effect(_)))

  def flatMap[B](f: A => IO[B]): IO[B] =
    IO.effect(f(self.unsafeInterpret()).unsafeInterpret())

}

object IO {
  def effect[A](eff: => A) = new IO(() => eff)
}

object MyIOApp extends App {

  def putStrLn(msg: String): IO[Unit] = IO.effect(println(msg))
  def getStrLn: IO[String]            = IO.effect(scala.io.StdIn.readLine())

  val prg = for {
    _    <- putStrLn("Hello, what's your name?")
    name <- getStrLn
    _    <- putStrLn(s"Hello, $name")
  } yield name

  prg.unsafeInterpret()

}

trait Console[F[_]] {
  def putStrLn(msg: String): F[Unit]
  def getStrLn: F[String]
}

object Console {
  def apply[F[_]](implicit f: Console[F]) = f
}

object MyIOConsoleApp {
  implicit val console = new Console[IO] {

    override def putStrLn(msg: String): IO[Unit] =
      IO.effect(println(msg))

    override def getStrLn: IO[String] =
      IO.effect(scala.io.StdIn.readLine())

  }
  /*
  def program[F[_]: Console]: F[String] =
    for {
      _    <- Console[F].putStrLn("What is your name?")
      name <- Console[F].getStrLn
      _    <- Console[F].putStrLn(s"Hello, $name")
    } yield name
   */
}
