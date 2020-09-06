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
import zio.console._
import java.io.File
import java.io.PrintWriter
import java.io.Writer

object EnuringSample extends App {

  def run(args: List[String]) =
    myAppLogic.exitCode

  private def deleteTempFile(file: File) =
    if (file.getName contentEquals "tmp.txt")
      putStrLn(s"del ${file.getName}") *> IO.effect {
        file.delete()
      }.ignore
    else ZIO.unit

  private def closeWriter(writer: Writer) =
    putStrLn("Closing writer") *> ZIO.effect(writer.close()).ignore

  private val myAppLogic = for {
    _        <- putStrLn("Hello! What is the filename?")
    filemane <- getStrLn
    _ <- ZIO(new File(s"/tmp/$filemane.txt")).bracket(deleteTempFile(_)) { file =>
      for {
        _    <- putStrLn("Hello! What is your name?")
        name <- getStrLn
        file <- ZIO(new PrintWriter(file)).bracket(closeWriter(_)) { writer =>
          ZIO(writer.append(s"Hello $name"))
        }
        _ <- putStrLn(s"Hello, ${name}, welcome to ZIO!")
      } yield ()
    }

  } yield ()

}
