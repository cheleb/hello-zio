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

package hellostm

import zio._
import zio.stm._

object HelloSTM extends ZIOAppDefault {

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    for {
      from <- TRef.makeCommit(10)
      to   <- TRef.makeCommit(1000)
      res  <- transfer(from, to, 100).ignore
      endz <- (from.get <*> to.get).commit
      (a, b) = endz
      _ <- Console.printLine(s"Balance is ($a, $b)  $res")
    } yield ()

  def deposit(accountBalance: TRef[Int], amount: Int): STM[Nothing, Unit] =
    accountBalance.update(_ + amount)

  def withdraw(accountBalance: TRef[Int], amount: Int): STM[String, Unit] =
    for {
      balance <- accountBalance.get
      _ <-
        if (balance < amount)
          STM.fail("Insufficient funds in you account")
        else
          accountBalance.update(_ - amount)
    } yield ()

  def transfer(from: TRef[Int], to: TRef[Int], amount: Int): IO[String, Unit] =
    STM.atomically {
      for {
        _ <- withdraw(from, amount)
        _ <- deposit(to, amount)
      } yield ()
    }
}
