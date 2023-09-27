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

package zymposium.zlayerderivation

import zio.*
import zio.ZLayer.Derive.Default

object ZLayerDerivation extends ZIOAppDefault {

  override def run: ZIO[Any & (ZIOAppArgs & Scope), Any, Any] =
    ZIO
      .service[ServiceA]
      .provide(
        ServiceA.manual,
        ServiceB.manual,
        ServiceC.manual
      )

}

trait ServiceA {
  def doSomething(): ZIO[Any, Nothing, Unit]
}

object ServiceA {

  final case class ServiceALive(
      serviceB: ServiceB,
      serviceC: ServiceC,
      promise: Promise[Nothing, Unit]
//      counter: Ref[Int]
  ) extends ServiceA {

    override def doSomething(): ZIO[Any, Nothing, Unit] =
      for {
        st  <- serviceB.doSomethingElse()
        st2 <- serviceC.doSomethingElseAgain()
//        c   <- counter.updateAndGet(_ + 1)
        _ <- ZIO.debug(s"Hello from ServiceA $st $st2 ")

      } yield ()

  }

  val manual: ZLayer[ServiceB & ServiceC, Nothing, ServiceALive] = ZLayer {
    for {
      promise <- Promise.make[Nothing, Unit]
//counter <- Ref.make(0)
      b <- ZIO.service[ServiceB]
      c <- ZIO.service[ServiceC]
    } yield ServiceALive(b, c, promise)
  }

  val auto: ZLayer[ServiceB & ServiceC, Nothing, ServiceALive] = {
    implicit val defaultInt: Default[Int] = ZLayer.Derive.Default.succeed(42)
    ZLayer.derive[ServiceALive]
  }

  val manual2 = ZLayer.fromFunction(ServiceALive.apply)

}
trait ServiceB {
  def doSomethingElse(): ZIO[Any, Nothing, String]
}

object ServiceB {

  final case class ServiceBLive() extends ServiceB {

    override def doSomethingElse(): ZIO[Any, Nothing, String] = ZIO.succeed("Hello from ServiceB")

  }

  val manual: ZLayer[Any, Nothing, ServiceBLive] = ZLayer.succeed(ServiceBLive())

}

trait ServiceC {
  def doSomethingElseAgain(): ZIO[Any, Nothing, Int]
}

object ServiceC {

  final case class ServiceCLive() extends ServiceC {

    override def doSomethingElseAgain(): ZIO[Any, Nothing, Int] = ZIO.succeed(42)

  }

  val manual: ZLayer[Any, Nothing, ServiceCLive] = ZLayer.succeed(ServiceCLive())
}
