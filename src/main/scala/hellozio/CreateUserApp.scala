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

object CreateUserApp extends zio.App {

  case class DBConnection(dbName: String)

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    def getUser(userId: UserId): ZIO[DBConnection, Nothing, Option[User]] = UIO(???)
    def createUser(user: User): ZIO[DBConnection, Nothing, Unit]          = UIO(???)

    val user: User = User(UserId(1234), "Chet")
    val created: ZIO[DBConnection, Nothing, Boolean] = for {
      maybeUser <- getUser(user.id)
      res       <- maybeUser.fold(createUser(user).as(true))(_ => ZIO.succeed(false))
    } yield res

    val dbConnection: DBConnection           = ???
    val runnable: ZIO[Any, Nothing, Boolean] = created.provide(dbConnection)
    runnable.map(_ => 0)
  }
}
