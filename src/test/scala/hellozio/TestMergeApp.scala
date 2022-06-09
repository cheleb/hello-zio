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
import scala.collection.immutable.Nil

object TestMergeApp extends App {

  val l1 = List(1, 5, 6, 12, 13, 14)
  val l2 = List(2, 4, 7, 11, 15, 17)
  val l3 = List(3, 8, 9, 10, 16)

  def merge(xl: List[List[Int]]): List[Int] = {
    val sorted = xl.filter(_.nonEmpty).sortBy(_.headOption)
    if (sorted.isEmpty)
      Nil
    else
      sorted(0)(0) :: merge(sorted.head.tail :: sorted.tail)

  }

  def merge2(xl: List[List[Int]]): List[Int] =
    xl.filter(_.nonEmpty).sortBy(_.headOption) match {
      case headList :: next =>
        headList.head :: merge(headList.tail :: next)
      case Nil => Nil
    }

  def fibo(x: Int) = {
    @annotation.tailrec
    def loop(current: Int, next: Int, n: Int): Int =
      n match {
        case 0 => current
        case _ => loop(next, current + next, n - 1)
      }

    loop(0, 1, x)
  }

  def fiboNaive(n: Int): Int =
    n match {
      case 0 => 0
      case 1 => 1
      case n => fiboNaive(n - 1) + fiboNaive(n - 2)
    }

  println(fibo(10))

  // println(merge2(List(l1, l2, l3)))

}
