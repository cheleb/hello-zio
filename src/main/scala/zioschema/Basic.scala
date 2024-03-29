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

package zioschema

import zio._
import zio.Console._

import zio.schema._

object Basic extends ZIOAppDefault {

  case class Age(i: Int) extends AnyVal

  case class Person(name: String, age: Int)

  val personSchema = DeriveSchema.gen[Person]

  val naturalSchema: Schema[Age] = Schema
    .primitive[Int]
    .transformOrFail(
      (age: Int) => Either.cond(age <= 120, Age(age), "Too old"),
      (age: Age) => Right(age.i)
    )
  override def run = printLine(personSchema.diff(Person("agnes", 50), Person("Agnes", 51)))

}
