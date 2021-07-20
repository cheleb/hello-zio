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

package zymposium

object OpticsDemo extends App {

  case class Rating(value: Int)
  case class Manager(name: String, rating: Rating)
  case class Developper(name: String, manager: Manager)

  case class Lens[Whole, Piece](get: Whole => Piece, set: Piece => Whole => Whole) { self =>
    def update(whole: Whole)(f: Piece => Piece): Whole = {
      val oldPiece = get(whole)
      set(f(oldPiece))(whole)
    }

    def >>>[SubPiece](that: Lens[Piece, SubPiece]): Lens[Whole, SubPiece] =
      Lens(
        whole => that.get(self.get(whole)),
        subPiece => whole => self.set(that.set(subPiece)(self.get(whole)))(whole)
      )
  }

  val manager = Lens[Developper, Manager](
    developper => developper.manager,
    manager => developper => developper.copy(manager = manager)
  )

  val rating: Lens[Manager, Rating] =
    Lens(_.rating, rating => manager => manager.copy(rating = rating))
  val upvote: Lens[Rating, Int] = Lens(_.value, value => rating => rating.copy(value = value))

  val myOptic = manager >>> rating >>> upvote

  val jane = Manager("Jane", Rating(10))
  val john = Developper("John", jane)

  val newJohn = myOptic.update(john)(_ + 1)

//  val newJohn = manager.update(john)(manager => manager.copy(name = manager.name.toUpperCase()))

  println(newJohn)

  type MyType = Either[String, Int]

  sealed trait Result

  case class Success(value: Int)     extends Result
  case class Failure(reason: String) extends Result

}
