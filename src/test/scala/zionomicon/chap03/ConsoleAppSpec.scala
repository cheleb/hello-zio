package zionomicon.chap03

import zio.test._
import zio.test.Assertion._
import zio.test.environment._
import zionomicon.chap3.ConsoleApp

object ConsoleAppSpec extends DefaultRunnableSpec {
  def spec =
    suite("ConsoleApp Spec")(
      testM("Greet say hi")(
        for {
          _      <- TestConsole.feedLines("Zozo")
          _      <- ConsoleApp.greet
          answer <- TestConsole.output
        } yield assert(answer)(equalTo(Vector(s"Hi Zozo\n")))
      )
    )
}
