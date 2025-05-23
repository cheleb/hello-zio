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

object Basic extends ZIOAppDefault {

  private val helloworld = printLine("Hello World") *> ZIO.sleep(1.second)
  override def run: ZIO[Environment & ZIOAppArgs & Scope, Any, Any] =
    helloworld
      *> helloworld
      *> printLine(".")
      *> ZIO.sleep(4.second)

}
