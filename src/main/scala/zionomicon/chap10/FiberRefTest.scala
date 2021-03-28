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

package zionomicon.chap10

import zio._

final case class Tree[+A](head: A, tail: List[Tree[A]])

object FiberRefTest extends App {

  type Log = Tree[Chunk[String]]
  val loggingRef: UIO[FiberRef[Log]] = FiberRef.make[Log](
    Tree(Chunk.empty, List.empty),
    _ => Tree(Chunk.empty, List.empty),
    (parent, child) => parent.copy(tail = child :: parent.tail)
  )

  def log(ref: FiberRef[Log])(string: String): UIO[Unit] =
    ref.update(log => log.copy(head = log.head :+ string))
  val program = for {
    ref <- loggingRef
    left = for {
      a <- ZIO.succeed(1).tap(_ => log(ref)("Got 1"))
      b <- ZIO.succeed(2).tap(_ => log(ref)("Got 2"))
    } yield a + b
    right = for {
      c <- ZIO.succeed(1).tap(_ => log(ref)("Got 3"))
      d <- ZIO.succeed(2).tap(_ => log(ref)("Got 4"))
    } yield c + d
    fiber1 <- left.fork
    fiber2 <- right.fork
    _      <- fiber1.join
    _      <- fiber2.join
    log    <- ref.get
    _      <- console.putStrLn(log.toString)
  } yield ()

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = program.exitCode

}
