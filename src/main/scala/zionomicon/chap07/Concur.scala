package zionomicon.chap07

import zio._
import zio.console._
import zio.duration._


object Concur extends App {

  private val zio1 = ZIO.sleep(100 millis) *> ZIO(1)
  private val zio2 = ZIO.sleep(500 millis) *> ZIO(2)

  private val program = for{
     res <- zio1.raceEither(zio2)
     _ <- putStrLn(s"Res: $res")
  } yield ()

  override def run(args: List[String]): URIO[ZEnv,ExitCode] = program.exitCode


}



object ValidatePar extends App {

  private def zioOf(i: Int) = ZIO.sleep(100*i millis) *> putStrLn(s"$i") *> (if(i % 3 == 0) ZIO.fail(i) else ZIO(i).orElseFail(0))

  private val program = for{
     res <- ZIO.validatePar(1 to 10)(zioOf)
     _ <- putStrLn(s"Res: $res")
  } yield ()

  override def run(args: List[String]): URIO[ZEnv,ExitCode] = program
     .catchAll{
         case e => putStrLnErr(e.toString())
     }
  .exitCode


}