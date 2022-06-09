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
import zio.managed._
import zio.Console._
import zio.ZIOAppDefault
import java.io.IOException

object AZIO {
  def anIO(i: Int): ZIO[Any, IOException, Unit] = printLine(s" 🚀 $i")

  def aLeakedIO(i: Int): ZIO[Scope, IOException, AutoCloseable] =
    anIO(i) *> ZIO.succeed(new AutoCloseable {

      override def close(): Unit = println(s" 🔥 $i")

    })
}

import AZIO._

object ZIOTest extends ZIOAppDefault {

  val program = for {
    _ <- aLeakedIO(1)
    _ <- anIO(2)
  } yield ()

  override def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    for {
      _ <- printLine("Start")
      _ <- program *> printLine(" ✅ 3")
      _ <- printLine("End")
    } yield ()
}

object ZManagedTest extends ZIOAppDefault {

  val managed = for {
    _ <-
      ZManaged
        .fromAutoCloseable(aLeakedIO(1))
    _ <-
      ZManaged
        .fromAutoCloseable(aLeakedIO(2))
  } yield ()

  val program = for {
    _ <- printLine("Start")
    _ <- managed.use(_ => printLine(s" ✅ 3"))
    _ <- printLine("End")
  } yield ()

  override def run = program

}

object ZUnManagedTest extends ZIOAppDefault {

  val managed: ZIO[Scope, IOException, Unit] =
    for {
      _ <- aLeakedIO(1).withFinalizerAuto
      _ <- aLeakedIO(2).withFinalizerAuto
    } yield ()
  val program = for {
    _ <- printLine("Start")
    _ <- ZIO.scoped(managed.flatMap(_ => printLine(s" ✅ 3")))
    _ <- printLine("End")
  } yield ()

  override def run = program

}
