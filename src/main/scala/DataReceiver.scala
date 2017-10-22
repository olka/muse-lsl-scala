package examples

import akka.NotUsed
import akka.actor.{Actor, ActorSystem}
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.{complete, get, pathEnd, pathPrefix}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import edu.ucsd.sccn.LSL
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.util.Try

case class EEG(tp9:Double, af7:Double, fpz:Double, af8:Double, tp10:Double)
object EEG{
  def apply(sample: Array[Double]) = new EEG(sample(0),sample(1),sample(2),sample(3),sample(4))
}

trait EEGProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val tickFormat: RootJsonFormat[EEG] = jsonFormat5(EEG.apply)
}

object DataReceiver extends EEGProtocol{
  implicit val system = ActorSystem("EEG_Receiver")
  implicit val materializer = ActorMaterializer()
  implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json().withParallelMarshalling(parallelism = 2, unordered = true)

  def receive: Source[EEG, NotUsed] ={
    println("Resolving an EEG stream...")
    val results = LSL.resolve_stream("type", "EEG")
    val inlet = new LSL.StreamInlet(results(0), 360, 0, false)
    val buffer = new Array[Double](inlet.info.channel_count)
    def next(state: Boolean) = {
      val isReceived = Try(inlet.pull_sample(buffer))
      val eeg = if(isReceived.isFailure) null else EEG(buffer)
      if (eeg == null) None
      else Some(true, eeg)
    }
    Source.unfold(true)(next)//.withAttributes(DefaultAttributes.buffer)
  }

  val route =
      pathPrefix("eegStream") {
        pathEnd {
          (get) (complete(receive))
        }
      }

  def main(args: Array[String]): Unit = {
    receive.runForeach(println)
  }
}