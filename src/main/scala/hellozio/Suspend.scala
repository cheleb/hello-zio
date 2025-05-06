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

object Suspend extends ZIOAppDefault {

  def sumZIO(a: Int): UIO[Int] =
    if a == 0 then ZIO.succeed(0)
    else
      for
        b <- sumZIO(a - 1)
        c <- ZIO.succeed(a)
      yield c + b

  override def run = sumZIO(100000).map(println)

}
