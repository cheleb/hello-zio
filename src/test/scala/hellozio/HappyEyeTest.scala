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

object HappyEyeTest extends ZIOAppDefault {

  val start = java.lang.System.currentTimeMillis()

  def log(msg: String): URIO[Console, Unit] =
    ZIO
      .succeed(java.lang.System.currentTimeMillis())
      .map(now => (now - start) / 1000L)
      .flatMap(elapsed => printLine(s"$elapsed $msg").orDie)

  def printSleepPrint(msg: String, delay: Duration): URIO[Console with Clock, Unit] =
    log(s"START: $msg") *> ZIO.sleep(delay) *> log(s"END: $msg")

  def printSleepFail(msg: String, delay: Duration): ZIO[Console with Clock, Throwable, Unit] =
    log(s"START: $msg") *> ZIO.sleep(delay) *> log(s"FAIL: $msg") *> ZIO.fail(
      new RuntimeException(s"FAIL: msg")
    )

  override def run: ZIO[Environment with ZEnv with ZIOAppArgs, Any, Any] =
    HappyEyeballs(
      List(
        printSleepPrint("task1", 10.second),
        printSleepFail("task2", 1.second),
        printSleepPrint("task3", 6.second),
        printSleepPrint("task4", 4.second)
      ),
      2.second
    )
      .tap(v => log(s"WON: $v"))
      .tapError(err => log(s"ERROR: $err"))
      .exitCode

}
