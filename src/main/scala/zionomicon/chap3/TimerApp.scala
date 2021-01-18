package zionomicon.chap3

import zio._
import zio.console._
import zio.clock._
import zio.duration._

object TimerApp extends App {

  val goShopping: ZIO[Console with Clock, Nothing, Unit] = putStrLn("Going shopping!").delay(1.hour)


  def run(args: List[String]): zio.URIO[zio.ZEnv,ExitCode] = goShopping.exitCode

}