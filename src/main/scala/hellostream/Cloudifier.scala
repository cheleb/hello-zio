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

package hellostream

import zio._
import zio.stream._
import scala.annotation.StaticAnnotation

@Accessible
trait Cloudifier {

  def stream: ZStream[Any, Nothing, Int]
  def sink: ZSink[Any, Nothing, Int, Nothing, Int]
}

case class Cloudifiered(i: Int) extends Cloudifier {
  def stream: ZStream[Any, Nothing, Int]           = ZStream.fromIterable(1 to 10)
  def sink: ZSink[Any, Nothing, Int, Nothing, Int] = ZSink.foldLeft(0)(_ + _)
}

object Cloudifier {

  def stream: ZStream[Cloudifier, Nothing, Int] = ZStream.serviceWithStream[Cloudifier](_.stream)

  def sink: ZSink[Cloudifier, Nothing, Int, Nothing, Int] =
    ZSink.serviceWithSink[Cloudifier](_.sink)

  val live: ZLayer[Any, Nothing, Cloudifier] = ZLayer.succeed(Cloudifiered(1))
}
