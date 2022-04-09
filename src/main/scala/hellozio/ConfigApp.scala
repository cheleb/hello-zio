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

import zio.config.magnolia.DeriveConfigDescriptor.descriptor
import java.net.URI

final case class Test(me: String, uri: URI)
final case class HelloConfig(port: Int, test: Test)

object ConfigApp extends ZIOAppDefault {

  override def run = ???

  private val configDescriptor = descriptor[HelloConfig]
  /*
  private val config = TypesafeConfig.fromTypesafeConfig(ConfigFactory.load(), configDescriptor)

  private val program = for {
    conf <- getConfig[HelloConfig]
    _    <- putStrLn(f"Hello on ${conf.port}%d ${conf.test} ")
  } yield ()
   */

}
