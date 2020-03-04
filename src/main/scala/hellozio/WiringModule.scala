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

import zio.ZIO
import hellozio.logging.Logging
import hellozio.userrepo.UserRepo
import hellozio.userrepo.DBError
import zio.ZLayer
import zio.console.Console
object WiringModule extends zio.App {

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = {
    val user2: User = User(UserId(123), "Tommy")
    val makeUser: ZIO[Logging with UserRepo, DBError, Unit] = for {
      _ <- Logging.info(s"inserting user") // ZIO[Logging, Nothing, Unit]
      _ <- UserRepo.createUser(user2)      // ZIO[UserRepo, DBError, Unit]
      _ <- Logging.info(s"user inserted")  // ZIO[Logging, Nothing, Unit]
    } yield ()

// compose horizontally
    val horizontal
        : ZLayer[Console, Nothing, Logging with UserRepo] = Logging.consoleLogger ++ UserRepo.live

// fulfill missing deps, composing vertically
    val fullLayer: ZLayer.NoDeps[Nothing, Logging with UserRepo] = Console.live >>> horizontal

// provide the layer to the program
    makeUser.provideLayer(fullLayer).fold(_ => 1, _ => 0)

  }

}
