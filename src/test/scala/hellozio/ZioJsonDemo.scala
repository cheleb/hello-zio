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
import zio.json.*

enum Color:
  case Red, Green, Blue, Yellow

object Color:
  given JsonDecoder[Color] = DeriveJsonDecoder.gen[Color]
  given JsonEncoder[Color] = DeriveJsonEncoder.gen[Color]

case class Baname(color: Color, curvature: Double)
object Baname {
  given JsonDecoder[Baname] = DeriveJsonDecoder.gen[Baname]
  given JsonEncoder[Baname] = DeriveJsonEncoder.gen[Baname]
}

object ZioJsonDemo extends ZIOAppDefault {

  override def run = {
    val b    = Baname(Color.Yellow, 0.5)
    val json = b.toJson
    val b2   = json.fromJson[Baname]
    Console.printLine(s"$b $json $b2")
  }

}
