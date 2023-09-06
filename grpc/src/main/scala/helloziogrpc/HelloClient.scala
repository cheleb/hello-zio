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

import zio._

import io.grpc.ManagedChannelBuilder
import zio.Console._
import scalapb.zio_grpc.ZManagedChannel

import io.grpc.examples.helloworld.helloworld.HelloRequest
import zio.ZIO
import io.grpc.Status
import io.grpc.examples.helloworld.helloworld.Corpus
import io.grpc.CallOptions
import java.util.concurrent.TimeUnit
import scalapb.zio_grpc.SafeMetadata
import io.grpc.examples.helloworld.helloworld.ZioHelloworld.GreeterClient

object ExampleClient extends zio.ZIOAppDefault {

  def clientLayer: Layer[Throwable, GreeterClient] =
    GreeterClient.live(
      ZManagedChannel(
        ManagedChannelBuilder.forAddress("localhost", 9000).usePlaintext()
      ),
      options = CallOptions.DEFAULT.withDeadlineAfter(3000, TimeUnit.MILLISECONDS)
      // headers = ZIO.succeed(SafeMetadata.make)
    )

  def myAppLogic =
    for {
      r <- GreeterClient.sayHello(HelloRequest("World", Corpus.LOCAL))
      _ <- printLine(r.message).orDie
    } yield ()

  override def run = myAppLogic.provide(clientLayer)
}
