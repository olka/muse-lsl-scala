package org.olka.eeg

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.server.Directives.{complete, get, pathEnd, pathPrefix}
import akka.stream.scaladsl.Source
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, ThrottleMode}
import edu.ucsd.sccn.LSL

import scala.concurrent.duration._
import scala.util.Try

object DataReceiver extends EEGProtocol {
  implicit val system = ActorSystem("EEG_Receiver")
  implicit val materializer = ActorMaterializer()
  implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json().withParallelMarshalling(parallelism = 2, unordered = true)

  def eegSource: Source[EEG, NotUsed] = {
    println("Resolving an EEG stream...")
    val results = LSL.resolve_stream("type", "EEG")
    val inlet = new LSL.StreamInlet(results(0), 360, 0, false)
    val buffer = new Array[Double](inlet.info.channel_count)

    def next(state: Boolean) = {
      val isReceived = Try(inlet.pull_sample(buffer))
      val eeg = if (isReceived.isFailure) null else EEG(buffer)
      if (eeg != null) MongoHelper.persist(eeg)
      if (eeg == null) None
      else Some(true, eeg)
    }

    Source.unfold(true)(next)
  }

  def eegSnapshot: List[EEG] = MongoHelper.getAll(0)


  val route =
    pathPrefix("eegStream") {
      pathEnd {
        (get) (complete(eegSource.throttle(18, 450.millis, 40, ThrottleMode.shaping)))
      }
    } ~
      pathPrefix("eegSnapshot") {
        pathEnd {
          (get) (complete(eegSnapshot))
        }
      }
}