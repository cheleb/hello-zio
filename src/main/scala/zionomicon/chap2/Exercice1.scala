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

import zio._

import zio.console._
import zio.clock.Clock

import java.io.IOException

object Exercice1 {
  def readFile(file: String): String = {
    val source = scala.io.Source.fromFile(file)
    try source.getLines().mkString
    finally source.close()
  }
  def readFileZio(file: String) =
    ZIO(scala.io.Source.fromFile(file)).bracketAuto { source =>
      ZIO.effect(source.getLines().mkString("\n"))
    }

  def writeFile(file: String, text: String): Unit = {
    import java.io._
    val pw = new PrintWriter(new File(file))
    try pw.write(text)
    finally pw.close
  }

  def writeFileZio(file: String, text: String) =
    ZIO(new java.io.PrintWriter(new java.io.File(file))).bracketAuto { writer =>
      ZIO.effect(writer.write(text))
    }
  /*
         ###### 3 #####
   */
  def copyFile(source: String, dest: String): Unit = {
    val contents = readFile(source)
    writeFile(dest, contents)
  }
  def copyFileZio(source: String, dest: String) =
    readFileZio(source)
      .flatMap(text => writeFileZio(dest, text))

  // #4
  def printLine(line: String) = ZIO.effect(println(line))
  val readLine                = ZIO.effect(scala.io.StdIn.readLine())

  printLine("What is your name?").flatMap(_ =>
    readLine.flatMap(name => printLine(s"Hello, ${name}!"))
  )

  for {
    _    <- printLine("What is your name?")
    name <- readLine
  } yield s"Hello, ${name}!"

  // #5

  val random = ZIO.effect(scala.util.Random.nextInt(3) + 1)
  random.flatMap(int =>
    printLine("Guess a number from 1 to 3:").flatMap(_ =>
      readLine.flatMap(num =>
        if (num == int.toString) printLine("You guessed right!")
        else printLine(s"You guessed wrong, the number was ${int}!")
      )
    )
  )

  for {
    int   <- random
    _     <- printLine("Guess a number from 1 to 3:")
    guess <- readLine
  } yield
    if (guess == int.toString) println("You guessed right!")
    else println(s"You guessed wrong, the number was ${int}!")

  // 11
  def eitherToZIO[E, A](either: Either[E, A]): ZIO[Any, E, A] =
    either match {
      case Left(value)  => ZIO.fail(value)
      case Right(value) => ZIO.succeed(value)
    }
  // 12
  def listToZIO[A](list: List[A]): ZIO[Any, None.type, A] =
    list match {
      case Nil       => ZIO.fail(None)
      case head :: _ => ZIO.succeed(head)
    }

  // 13
  lazy val currentTimeZIO: ZIO[Any, Nothing, Long] =
    ZIO.effectTotal(System.currentTimeMillis())

  // 14
  def getCacheValue(
      key: String,
      onSuccess: String => Unit,
      onFailure: Throwable => Unit
  ): Unit = ???
  def getCacheValueZio(key: String): ZIO[Any, Throwable, String] =
    ZIO.effectAsync { callback =>
      getCacheValue(key, str => callback(ZIO.succeed(str)), th => callback(ZIO.die(th)))
    }

  // 15

  trait User
  def saveUserRecord(
      user: User,
      onSuccess: () => Unit,
      onFailure: Throwable => Unit
  ): Unit = ???

  def saveUserRecordZio(user: User): ZIO[Any, Throwable, Unit] =
    ZIO.effectAsync { callback =>
      saveUserRecord(user, () => callback(ZIO.succeed(())), th => callback(ZIO.die(th)))
    }

  // 16

  import scala.concurrent.{ ExecutionContext, Future }
  trait Query
  trait Result
  def doQuery(query: Query)(implicit ec: ExecutionContext): Future[Result] =
    ???
  def doQueryZio(query: Query): ZIO[Any, Throwable, Result] =
    ZIO.fromFuture(implicit ec => doQuery(query))

  // 19
  def readUntil(
      acceptInput: String => Boolean
  ): ZIO[Console, IOException, String] =
    for {
      str <- getStrLn.repeatUntil(acceptInput)
    } yield str

  def doWhile[R, E, A](body: ZIO[R, E, A])(condition: A => Boolean): ZIO[R, E, A] =
    for {
      a   <- body
      res <- if (condition(a)) body else ZIO.succeed(a)
    } yield res
}

object Cat extends App {
  def run(commandLineArguments: List[String]) =
    ZIO
      .foreach(commandLineArguments)(filename =>
        Exercice1
          .readFileZio(filename)
          .tap(str => IO(println(str)))
      )
      .exitCode
}

// 17

object HelloHuman extends App {

  val p = putStrLn("What is your name?").flatMap { _ =>
    val oo = putStrLn(s"Hello coco")

    getStrLn.flatMap { name =>
      putStrLn(s"Hello $name")
    }
  }

  private val program = for {
    _    <- putStrLn("What is your name?")
    name <- getStrLn
    _    <- putStrLn(s"Hello $name")
  } yield ()

  def run(args: List[String]) =
    p.exitCode
}

// 18
import zio.random._

object NumberGuessing extends App {

  private lazy val readInt = for {
    line <- getStrLn
    int  <- ZIO.effect(line.toInt)
  } yield int

  private lazy val readIntAndRetry: URIO[Console, Int] =
    readInt
      .orElse(
        putStrErr("Not a valid integer...")
        *> readIntAndRetry
      )

  private def makeAGuess(secret: Int): ZIO[Console with Clock, Throwable, Int] =
    for {
      guess <-
        readIntAndRetry //.flatMap(str => ZIO.fromTry(Try(str.toInt))).retry(Schedule.forever)
      _ <-
        if (guess < secret)
          putStrLn("To low") *> makeAGuess(secret)
        else if (guess > secret)
          putStrLn("To High") *> makeAGuess(secret)
        else
          putStr("Won !")
    } yield guess

  private val program = for {
    secret <- nextIntBounded(100)
    _      <- putStrLn("Guess a number?")
    _      <- makeAGuess(secret)
  } yield ()

  def run(args: List[String]) =
    program.exitCode
}
