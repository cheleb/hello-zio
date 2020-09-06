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

import io.grpc.Status
import scalapb.zio_grpc.ServerMain
import scalapb.zio_grpc.ServiceList
import zio.{ ZEnv, ZIO }
import zio.console._

import io.grpc.examples.helloworld.helloworld.ZioHelloworld.ZGreeter
import io.grpc.examples.helloworld.helloworld.{ HelloReply, HelloRequest }

object GreeterImpl extends ZGreeter[ZEnv, Any] {
  def sayHello(
      request: HelloRequest
  ): ZIO[zio.ZEnv, Status, HelloReply] =
    putStrLn(s"Got request: ${request.name}") *>
    ZIO.succeed(HelloReply(s"Hello, ${request.name}"))
}

object HelloWorldServer extends ServerMain {
  def services: ServiceList[zio.ZEnv] = ServiceList.add(GreeterImpl)
}
