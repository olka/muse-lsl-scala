package org.olka.eeg

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class EEG(tp9:Double, af7:Double, fpz:Double, af8:Double, tp10:Double)

object EEG{
  def apply(sample: Array[Double]) = new EEG(sample(0),sample(1),sample(2),sample(3),sample(4))
}

trait EEGProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val eegFormat: RootJsonFormat[EEG] = jsonFormat5(EEG.apply)
}
