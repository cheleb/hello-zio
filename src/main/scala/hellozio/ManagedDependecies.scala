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

import zio.console.Console

import userrepo.UserRepo
import hellozio.userrepo.DBError
import hellozio.logging.Logging

object ManagedDependecies extends zio.App {

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    import java.sql.Connection
    def makeConnection: UIO[Connection] = UIO(???)
    val connectionLayer: ZLayer.NoDeps[Nothing, Has[Connection]] =
      ZLayer.fromAcquireRelease(makeConnection)(c => UIO(c.close()))
    val postgresLayer: ZLayer[Has[Connection], Nothing, UserRepo] =
      ZLayer.fromFunction { hasC =>
        new UserRepo.Service {
          override def getUser(userId: UserId): IO[DBError, Option[User]] = UIO(???)
          override def createUser(user: User): IO[DBError, Unit]          = UIO(???)
        }
      }

    val horizontal: ZLayer.NoDeps[Nothing, Logging] = Console.live >>>
      Logging.consoleLogger

    val fullRepo: ZLayer.NoDeps[Nothing, UserRepo with Logging] = connectionLayer >>> horizontal ++ postgresLayer

    val user2: User = User(UserId(123), "Tommy")

    val makeUser: ZIO[UserRepo with Logging, DBError, Unit] = for {
      _ <- Logging.info(s"inserting user") // ZIO[Logging, Nothing, Unit]
      _ <- UserRepo.createUser(user2)      // ZIO[UserRepo, DBError, Unit]
      _ <- Logging.info(s"user inserted")  // ZIO[Logging, Nothing, Unit]
    } yield ()

    makeUser.provideLayer(fullRepo).fold(_ => 1, _ => 0)

  }

}
