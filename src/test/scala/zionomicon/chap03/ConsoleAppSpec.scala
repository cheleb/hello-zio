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

package zionomicon.chap03

import zio.test._
import zio.test.Assertion._

import zionomicon.chap3.ConsoleApp

object ConsoleAppSpec extends ZIOSpecDefault {
  def spec =
    suite("ConsoleApp Spec")(
      test("Greet say hi")(
        for {
          _      <- TestConsole.feedLines("Zozo")
          _      <- ConsoleApp.greet
          answer <- TestConsole.output
        } yield assert(answer)(equalTo(Vector(s"Who are you?\n", "Hi Zozo\n")))
      )
    )
}
