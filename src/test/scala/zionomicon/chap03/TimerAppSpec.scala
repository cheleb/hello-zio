package zionomicon.chap03

import zio.test._
import zio.duration._
import zio.test.environment._
import zionomicon.chap3.TimerApp.goShopping

object TimerAppSpec extends DefaultRunnableSpec {
  def spec =
    suite("Timer")(
      testM("goShopping delays for one hour") {
        for {
          fiber <- goShopping.fork
          _     <- TestClock.adjust(1.hour)
          _     <- fiber.join
        } yield assertCompletes
      }
    )
}
