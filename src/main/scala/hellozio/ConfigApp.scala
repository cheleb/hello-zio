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
import zio.config._
import zio.config.magnolia.descriptor
import java.net.URI

import com.typesafe.config.ConfigFactory
import zio.config.typesafe._

final case class Test(me: String, uri: URI)
final case class HelloConfig(port: Int, test: Test)

object ConfigApp extends ZIOAppDefault {

  override def run = program

  private implicit val configDescriptor: config.ConfigDescriptor[HelloConfig] =
    descriptor[HelloConfig]

  private val program = for {
    conf <- read(
      configDescriptor from ConfigSource.fromTypesafeConfig(
        ZIO.attempt(ConfigFactory.defaultApplication())
      )
    )
    _ <- Console.printLine(f"Hello on ${conf.port}%d ${conf.test} ")
  } yield ()

}
