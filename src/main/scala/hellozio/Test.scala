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

trait UserRepo {
  def getIt(id: Int): Task[String]
}

object UserRepo extends UserRepo {

  override def getIt(id: Int): Task[String] = ???

}

case class PostgreSQLRepo(dbname: String) extends UserRepo {

  override def getIt(id: Int): Task[String] = ZIO.succeed(s"Agnes $id")

}

object PostgreSQLRepo {
  val layer = ZLayer {
    ZIO.succeed(new PostgreSQLRepo("olivier"))
  }
}

object TestApp extends ZIOAppDefault {

  val program: ZIO[UserRepo, Throwable, Unit] = for {
    _    <- printLine("Accessible rocks")
    name <- UserRepo.getIt(1)
    _    <- printLine(s"Hello $name")
  } yield ()

  override def run =
    program
      .provide(PostgreSQLRepo.layer)
      .exitCode

}
