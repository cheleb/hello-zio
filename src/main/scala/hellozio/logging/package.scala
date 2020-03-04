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

package object logging {
  type Logging = Has[Logging.Service]

  object Logging {
    trait Service {
      def info(s: String): UIO[Unit]
      def error(s: String): UIO[Unit]
    }

    import zio.console.Console
    val consoleLogger: ZLayer[Console, Nothing, Logging] = ZLayer.fromFunction(
      console =>
        new Service {
          def info(s: String): UIO[Unit]  = console.get.putStrLn(s"info - $s")
          def error(s: String): UIO[Unit] = console.get.putStrLn(s"error - $s")
        }
    )

    //accessor methods
    def info(s: String): ZIO[Logging, Nothing, Unit] =
      ZIO.accessM(_.get.info(s))

    def error(s: String): ZIO[Logging, Nothing, Unit] =
      ZIO.accessM(_.get.error(s))
  }
}
