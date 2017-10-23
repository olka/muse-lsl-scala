package org.stanoq.tests.crawler

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import mock.DataSender
import org.olka.eeg.DataReceiver
import org.scalatest.{FlatSpec, Matchers}

import concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}


class ReceiverTest extends FlatSpec with Matchers {
  implicit val system = ActorSystem("EEG_Receiver")
  implicit val context = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

  implicit val materializer = ActorMaterializer()

  "Receiver" should "be able to receive LSL packets" in {
    Future { DataSender.sendData(150) } //    Future {DataReceiver.receive.runForeach(println)}
    val res =  Await.result(DataReceiver.eegSource.runFold(0)((c,e)=>c+1), 15 seconds)
    res should be >20
  }
}
