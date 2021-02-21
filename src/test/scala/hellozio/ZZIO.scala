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

package hellozio

class T[-A] {
//  def add(a: A): Unit = ???
}

object TestT extends App {

  trait A

  trait B //extends A

  type AB = B with A

  val ab: AB = ???

  val a: A = ab

  val t_a: T[A] = ???
//  val t_B: T[B] = t_a

  val list = List(new T[A], new T[B])

}

case class ZZIO[-R, +E, +A](run: R => Either[E, A]) { self =>

  def map[B](f: A => B): ZZIO[R, E, B] =
    ZZIO(r => self.run(r).map(f))

  def flatMap[R1 <: R, E1 >: E, B](f: A => ZZIO[R1, E1, B]): ZZIO[R1, E1, B] =
    ZZIO { r =>
      val z  = self.run(r).fold(ZZIO.fail(_), f)
      val zz = z.run(r)
      zz
    }

}

object ZZIO {
  def fail[R, E, A](e: E): ZZIO[R, E, A] = ???
}

object ZZIOApp extends App {

  def zio1         = ZZIO[Int, Nothing, Int](r => Right(r * 10))
  def zio2(i: Int) = ZZIO[String, Nothing, Int](r => Right(r.size + i))

  val res = for {
    a <- zio1
    b <- zio2(a)
  } yield a + b

  val res2 = zio1
    .flatMap { a =>
      zio2(a).map(b => a + b)
    }

  val res4 =
    ZZIO((r: Int with String) =>
      zio1.run(r).fold(ZZIO.fail(_), a => zio2(a).map(b => a + b)).run(r)
    )

}
