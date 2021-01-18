package zionomicon.chap3

import zio._
import zio.console._
object ConsoleApp extends App {

  val greet = for {
    name <- getStrLn
    _    <- putStrLn(s"Hi $name")
  } yield ()

  override def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] = greet.exitCode

}
