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

package object userrepo {

  case class DBError(msg: String)

  type UserRepo = Has[UserRepo.Service]

  object UserRepo {

    trait Service {
      def getUser(userId: UserId): IO[DBError, Option[User]]
      def createUser(user: User): IO[DBError, Unit]
    }
    val live: ZLayer.NoDeps[Nothing, UserRepo] = ZLayer.succeed {
      new Service {
        def getUser(userId: UserId): IO[DBError, Option[User]] = ???
        def createUser(user: User): IO[DBError, Unit]          = ???
      }
    }

    def getUser(userId: UserId): ZIO[UserRepo, DBError, Option[User]] =
      ZIO.accessM(_.get.getUser(userId))

    def createUser(user: User): ZIO[UserRepo, DBError, Unit] =
      ZIO.accessM(_.get.createUser(user))
  }

}
