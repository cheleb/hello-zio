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
import zio.test._
import zio.random.Random
import Assertion._

object LayerTests {

  val firstNames = Vector("Ed", "Jane", "Joe", "Linda", "Sue", "Tim", "Tom")

  type Names = Has[Names.Service]

  object Names {
    trait Service {
      def randomName: UIO[String]
    }

    case class NamesImpl(randomService: Random.Service) extends Service {
      println("Created nameImpl")
      def randomName: UIO[String] =
        randomService.nextIntBounded(firstNames.size).map(firstNames(_))
    }

    val live = ZLayer.fromService(NamesImpl)
  }

  def namesTest =
    testM("names test") {
      for (name <- names.randomName) yield assert(firstNames.contains(name))(equalTo(true))
    }

}

import LayerTests._

package object names {
  def randomName = ZIO.access[Names](_.get.randomName)
}
