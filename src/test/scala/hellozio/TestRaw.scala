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

object TestRaw extends App {

  def wtf(connectionOpt: Option[Boolean], placementOpt: Option[Boolean]): Option[String] =
    (connectionOpt, placementOpt) match {
      case (Some(connection), Some(placement)) if connection =>
        Some("Yo")
      case (Some(connection), _) => Some("Yo")
      case _                     => None
    }

  def wtf2(connectionOpt: Option[Boolean], placementOpt: Option[Boolean]): Option[String] =
    (connectionOpt, placementOpt) match {
      case (Some(connection), _) => Some("Yo")
      case _                     => None
    }

  List(Some(true), Some(false), None, None).combinations(2).flatMap(_.permutations).foreach {
    case a :: b :: Nil =>
      println(s" wtf($a, $b) == wtf2($a,$b) ${wtf(a, b) == wtf2(a, b)}")
    case _ => ???
  }

}
