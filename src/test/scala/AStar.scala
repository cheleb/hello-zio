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

object AStar extends App {

  val matrix = Array.ofDim[Int](10, 10)

  matrix(4) = Array(0, 0, 1, 1, 1, 1, 1, 1, 1, 0)

  printMatrix(matrix)

  def aStar(start: (Int, Int), end: (Int, Int)): Seq[(Int, Int)] = ???

  def printMatrix(matrix: Array[Array[Int]]): Unit =
    matrix.zipWithIndex.foreach { case (line, i) =>
      print(s"$i: ")
      println(
        line
          .map {
            case 0 => '.'
            case 1 => 'O'
          }
          .mkString("")
      )
    }

}
