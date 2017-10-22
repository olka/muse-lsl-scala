package org.stanoq.tests.crawler

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import examples.DataReceiver
import mock.DataSender
import org.scalatest.{AsyncFlatSpec, FlatSpec, Matchers}
import concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}


class ReceiverTest extends AsyncFlatSpec with Matchers {
  implicit val system = ActorSystem("EEG_Receiver")
  implicit val context = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

  implicit val materializer = ActorMaterializer()

  "Receiver" should "be able to receive LSL packets" in {
    Future { DataReceiver.receive.runForeach(println)}
    Await.ready(Future { DataSender.sendData(100) }, 2.seconds)
    succeed
  }
}
