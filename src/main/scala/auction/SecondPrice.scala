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

package auction

import zio._

case class Player(name: String, bids: List[Int])
case class Winner(players: List[Player], price: Int)

object SecondPrice extends App {

  def players = ZIO(List(Player("A", List(30, 100))))

  /*
  def enroll(player: Player, winner: Ref[Winner]) = for {
       _ <- ZIO.when(player.bids.max > winner.get.price)(winner.update(winner.))

  }
   */
  def program =
    for {
      ref     <- Ref.make(Winner(List.empty, 100))
      players <- players
      //    _       <- ZIO.foreach(players)
    } yield ref.get

  def run(args: List[String]): URIO[ZEnv, ExitCode] = ???

}
