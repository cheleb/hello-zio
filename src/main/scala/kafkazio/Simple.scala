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

package kafkazio

import zio._
import zio.console._

import zio.duration._
import zio.kafka.consumer._
import zio.blocking.Blocking, zio.clock.Clock
import zio.kafka.consumer.{ Consumer, ConsumerSettings }
import zio.kafka.serde._
import zio.kafka.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord

import zio.kafka.producer.ProducerSettings

object Simple extends App {

  val consumerSettings: ConsumerSettings =
    ConsumerSettings(List("localhost:9092"))
      .withGroupId("group")
      .withClientId("client")
      .withCloseTimeout(30.seconds)

  val producerSettings: ProducerSettings = ProducerSettings(List("localhost:9092"))

  lazy val consumerManaged: ZManaged[Clock with Blocking, Throwable, Consumer.Service] =
    Consumer.make(consumerSettings)

  lazy val consumerL =
    ZLayer.fromManaged(consumerManaged) ++
    ZLayer.fromManaged(Producer.make(producerSettings, Serde.string, Serde.string))

  val subscription = Subscription.topics("test-in")

  def consume: ZIO[zio.ZEnv, Throwable, Unit] =
    Consumer
      .subscribeAnd(subscription)
      .plainStream(Serde.string, Serde.string)
      .tap(record => putStrLn(s"Received message ${record.record.key()}: ${record.record.value()}"))
      .map { record =>
        val key: String      = record.record.key()
        val value: String    = record.record.value()
        val newValue: String = value.toString
        val producerRecord: ProducerRecord[String, String] =
          new ProducerRecord("test-out", key, newValue)
//

        (producerRecord, record.offset)
      }
      .mapChunksM { chunk =>
        val records     = chunk.map(_._1)
        val offsetBatch = OffsetBatch(chunk.map(_._2).toSeq)
        Producer.produceChunk[Any, String, String](records) *> offsetBatch.commit.as(Chunk(()))
      }
      .runDrain
      .provideCustomLayer(consumerL)
  /*
  val consumer = Consumer.consumeWith(consumerSettings, subscription, Serde.string, Serde.string) {
    case (key, value) =>
      putStrLn(s"Received message ${key}: ${value}")
    // Perform an effect with the received message
  }
   */
  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    consume.exitCode

}
