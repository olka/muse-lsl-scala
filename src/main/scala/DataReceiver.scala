package examples

import akka.NotUsed
import akka.actor.{Actor, ActorSystem}
import akka.stream.ActorMaterializer
import akka.stream.impl.ActorPublisher
import akka.stream.scaladsl.Source
import edu.ucsd.sccn.LSL

case class EEG(tp9:Double, af7:Double, fpz:Double, af8:Double, tp10:Double)
object EEG{
  def apply(sample: Array[Double]) = new EEG(sample(0),sample(1),sample(2),sample(3),sample(4))
}

object DataReceiver{
  implicit val system = ActorSystem("EEG_Receiver")
  implicit val materializer = ActorMaterializer()

  def receive: Source[EEG, NotUsed] ={
    println("Resolving an EEG stream...")
    val results = LSL.resolve_stream("type", "EEG")
    val inlet = new LSL.StreamInlet(results(0))
    val sample = new Array[Double](inlet.info.channel_count)
    def receiveEeg = {inlet.pull_sample(sample);EEG(sample)}
    Source.repeat[EEG](receiveEeg)
  }

  def main(args: Array[String]): Unit = {
    receive.runForeach(println)
  }
}