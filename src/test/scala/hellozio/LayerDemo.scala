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

import zio._

package object logging { // Module definition
  type Logging = Has[Logging.Service]
  object Logging {
    // Service definition
    trait Service {
      def logLine(line: String): UIO[Unit]
    }
    // Module implementation
    val console: ZLayer[Any, Nothing, Logging] = ZLayer.succeed {
      new Service {
        def logLine(line: String): UIO[Unit] =
          UIO.effectTotal(println(line))
      }
    }
  }
  // Accessor methods
  def logLine(line: String): URIO[Logging, Unit] = ZIO.accessM(_.get.logLine(line))
}
