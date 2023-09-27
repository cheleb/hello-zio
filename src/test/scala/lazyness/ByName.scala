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

package lazyness

def byNameCall(i: => Int): Unit = {
  println(i)
  println(i)
}

def byValueCall(i: Int): Unit = {
  println(s"->$i")
  println(s"->$i")
}

object ByName extends App {
  var helloCounter = 0

  def say2Hello: Int = {
    println(s"Hello n°$helloCounter")
    helloCounter += 1
    helloCounter
  }
  byNameCall(say2Hello)
  // println(helloCounter)
  //
  println("--------------")
  //
  byValueCall(say2Hello)
  println(helloCounter)
}
