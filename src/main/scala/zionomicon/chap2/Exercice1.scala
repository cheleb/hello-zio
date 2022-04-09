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

import zio.Console._

import zio.ZIOAppDefault

object Exercice1 {
  def readFile(file: String): String = {
    val source = scala.io.Source.fromFile(file)
    try source.getLines().mkString
    finally source.close()
  }
  def readFileZio(file: String) =
    ZIO.attempt(scala.io.Source.fromFile(file)).acquireReleaseWithAuto { source =>
      ZIO.attempt(source.getLines().mkString("\n"))
    }

  def writeFile(file: String, text: String): Unit = {
    import java.io._
    val pw = new PrintWriter(new File(file))
    try pw.write(text)
    finally pw.close
  }

  def writeFileZio(file: String, text: String) =
    ZIO.attempt(new java.io.PrintWriter(new java.io.File(file))).acquireReleaseWithAuto { writer =>
      ZIO.attempt(writer.write(text))
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
  def printLine(line: String) = ZIO.attempt(println(line))
  val readLine                = ZIO.attempt(scala.io.StdIn.readLine())

  printLine("What is your name?").flatMap(_ =>
    readLine.flatMap(name => printLine(s"Hello, ${name}!"))
  )

  for {
    _    <- printLine("What is your name?")
    name <- readLine
  } yield s"Hello, ${name}!"

  // #5

  val random = ZIO.attempt(scala.util.Random.nextInt(3) + 1)
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
    ZIO.succeed(java.lang.System.currentTimeMillis())

  // 14
  def getCacheValue(
      key: String,
      onSuccess: String => Unit,
      onFailure: Throwable => Unit
  ): Unit = ???
  def getCacheValueZio(key: String): ZIO[Any, Throwable, String] =
    ZIO.async { callback =>
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
    ZIO.async { callback =>
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
  ): ZIO[Any, Throwable, String] =
    for {
      str <- readLine.repeatUntil(acceptInput)
    } yield str

  def doWhile[R, E, A](body: ZIO[R, E, A])(condition: A => Boolean): ZIO[R, E, A] =
    for {
      a   <- body
      res <- if (condition(a)) body else ZIO.succeed(a)
    } yield res
}

/**
  * object Cat extends ZIOAppDefault {
  *  override def run =
  *    (for {
  *      args <- getArgs
  *      _ <-
  *        ZIO
  *          .foreach(args)(filename =>
  *            Exercice1
  *              .readFileZio(filename)
  *              .tap(str => IO(println(str)))
  *          )
  *    } yield ()).provide(Scope.default)
  *
  * }
  */
// 17

object HelloHuman extends ZIOAppDefault {

  val p = printLine("What is your name?").flatMap { _ =>
    val oo = printLine(s"Hello coco")

    readLine.flatMap { name =>
      printLine(s"Hello $name")
    }
  }

  private val program = for {
    _    <- printLine("What is your name?")
    name <- readLine
    _    <- printLine(s"Hello $name")
  } yield ()

  override def run =
    p
}

// 18
import zio.Random._

object NumberGuessing extends ZIOAppDefault {

  private lazy val readInt = for {
    line <- readLine
    int  <- ZIO.attempt(line.toInt)
  } yield int

  private lazy val readIntAndRetry: URIO[Any, Int] =
    readInt
      .orElse(
        printLineError("Not a valid integer...").orDie
        *> readIntAndRetry
      )

  private def makeAGuess(secret: Int): ZIO[Any, Throwable, Int] =
    for {
      guess <-
        readIntAndRetry //.flatMap(str => ZIO.fromTry(Try(str.toInt))).retry(Schedule.forever)
      _ <-
        if (guess < secret)
          printLine("Too low") *> makeAGuess(secret)
        else if (guess > secret)
          printLine("To High") *> makeAGuess(secret)
        else
          printLine("Won !")
    } yield guess

  private val program: ZIO[Any, Throwable, Unit] = for {
    secret <- nextIntBounded(100)
    _      <- printLine("Guess a number?")
    res    <- makeAGuess(secret).timeout(5.seconds)

  } yield ()

  override def run =
    program.disconnect.timeout(2.second)
}
