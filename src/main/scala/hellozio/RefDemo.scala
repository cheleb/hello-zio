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

import zio.*

object RefDemo extends ZIOAppDefault {

  private val program = for {
    ref <- Ref.make(0)
    _   <- ZIO.foreachPar(1 to 1000)(i => Console.printLine(i) *> ref.update(_ + 1))
    v   <- ref.get

    _ <- Console.printLine(s"Value: $v")
  } yield ()

  override def run: ZIO[Any & (ZIOAppArgs & Scope), Any, Any] = program

}
