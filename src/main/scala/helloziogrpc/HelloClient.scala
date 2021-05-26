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

package helloziogrpc

import io.grpc.ManagedChannelBuilder
import zio.console._
import scalapb.zio_grpc.ZManagedChannel

import zio.Layer

import io.grpc.examples.helloworld.helloworld.ZioHelloworld.GreeterClient
import io.grpc.examples.helloworld.helloworld.HelloRequest
import zio.ZIO
import io.grpc.Status

object ExampleClient extends zio.App {

  def clientLayer: Layer[Throwable, GreeterClient] =
    GreeterClient.live(
      ZManagedChannel(
        ManagedChannelBuilder.forAddress("localhost", 9000).usePlaintext()
      )
    )

  def myAppLogic: ZIO[GreeterClient with Console, Status, Unit] =
    for {
      r <- GreeterClient.sayHello(HelloRequest("World"))
      _ <- putStrLn(r.message).orDie
    } yield ()

  final def run(args: List[String]) =
    myAppLogic.provideCustomLayer(clientLayer).exitCode
}
