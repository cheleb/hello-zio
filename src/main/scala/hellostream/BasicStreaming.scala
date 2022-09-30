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

class Accessible extends StaticAnnotation

object BasicStreaming extends ZIOAppDefault {

  val program = for {
    _ <- MyZStreamer.stream
      .map(_.toString)
      .foreach(Console.printLine(_))
    _   <- Console.printLine("Hello, World!")
    res <- MyZStreamer.stream >>> MyZStreamer.sink
    _   <- Console.printLine(s"Result: $res")
  } yield 0

  def run = program.provideLayer(MyZStreamer.live)

}

@Accessible
trait MyZStreamer {
  def stream: ZStream[Any, Nothing, Int]
  def sink: ZSink[Any, Nothing, Int, Nothing, Int]
}

case class MyZStream(i: Int) extends MyZStreamer {
  def stream: ZStream[Any, Nothing, Int]           = ZStream.fromIterable(1 to 10)
  def sink: ZSink[Any, Nothing, Int, Nothing, Int] = ZSink.foldLeft(0)(_ + _)
}

object MyZStreamer {
  def stream: ZStream[MyZStreamer, Nothing, Int] = ZStream.serviceWithStream[MyZStreamer](_.stream)
  def sink: ZSink[MyZStreamer, Nothing, Int, Nothing, Int] =
    ZSink.serviceWithSink[MyZStreamer](_.sink)

  val live: ZLayer[Any, Nothing, MyZStreamer] = ZLayer.succeed(MyZStream(1))
}
