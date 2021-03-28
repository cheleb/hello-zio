package zionomicon.chap13

import zio._
import zio.clock._
import zio.console._
import zio.duration._

object SemaphoreDemo extends App {

  def queryDatabase(connections: Ref[Int]): URIO[Console with Clock, Unit] =
    connections.updateAndGet(_ + 1).flatMap { n =>
      console.putStrLn(s"Aquiring connection, now $n simultaneous connections") *>
      ZIO.sleep(1.second) *>
      console.putStrLn(s"Closing connection, now ${n - 1} simultaneous connections")
    }

  val program = for {
    ref       <- Ref.make(0)
    semaphore <- Semaphore.make(4)

    _ <- ZIO.foreachPar_(1 to 10)(_ => semaphore.withPermit(queryDatabase(ref)))
  } yield ()

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = program.exitCode

}
