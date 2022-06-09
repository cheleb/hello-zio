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

import zio._

import zio.Console._

import java.io.File
import java.io.PrintWriter
import java.io.Writer

object EnsuringSample extends ZIOAppDefault {

  override def run = myAppLogic

  private def deleteTempFile(file: File): ZIO[Any, Nothing, AnyVal] =
    if (file.getName contentEquals "tmp.txt")
      (printLine(s"del ${file.getName}") *> ZIO.sleep(2 seconds) *> ZIO.attempt {
        file.delete()
      }).orDie
    else ZIO.unit

  private def closeWriter(writer: Writer) =
    (printLine("Closing writer") *> ZIO.attempt(writer.close())).orDie

  private def newFile(filename: String) =
    ZIO.attempt(new File(s"/tmp/$filename.txt")).withFinalizer(deleteTempFile(_))

  private def safeWriter(file: File) =
    ZIO.attempt(new PrintWriter(file)).withFinalizer(closeWriter(_))

  private val myAppLogic = for {
    _        <- printLine("Hello! What is the filename?")
    filename <- readLine
    _ <- newFile(filename).map { file =>
      for {
        _    <- printLine("Hello! What is your name?")
        name <- readLine
        _ <- safeWriter(file).map { writer =>
          ZIO.attempt(writer.append(s"Hello $name\n")).repeatN(10)
        }
        _ <- printLine(s"Hello, ${name}, welcome to ZIO!")
      } yield ()
    }

  } yield ()

}
