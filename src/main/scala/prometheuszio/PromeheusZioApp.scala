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

package prometheuszio

import zio._
import zio.metrics.prometheus._
import zio.metrics.prometheus.helpers._
import zio.metrics.prometheus.exporters.Exporters

import zio.Console._

object PromeheusZioApp extends App {

  val prometheusLayer = Registry.live ++ Exporters.live

  val testCounter = for {
    _ <- printLine("Helo")
    c <- Counter("PrometheusTest", Array.empty[String])
    _ <- c.inc()
    _ <- c.inc(2.0)
    r <- getCurrentRegistry()

  } yield r

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    testCounter.provideCustomLayer(prometheusLayer).exitCode

}
