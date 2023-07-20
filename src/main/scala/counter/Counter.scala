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

package counter

import zio.*

import hellostream.Accessible

@Accessible
trait Counter {
  def inc: UIO[Unit]
  def dec: UIO[Unit]
}
class CounterImpl2 extends Counter {
  def dec: UIO[Unit] = ???
  def inc: UIO[Unit] = ???
}

object Counter {
  def inc: URIO[Counter, Unit] = ZIO.serviceWithZIO[Counter](_.inc)
  def dec: URIO[Counter, Unit] = ZIO.serviceWithZIO[Counter](_.dec)
}
