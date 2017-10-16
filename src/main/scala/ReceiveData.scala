package examples

import edu.ucsd.sccn.LSL

object ReceiveData extends App {
    println("Resolving an EEG stream...")
    val results = LSL.resolve_stream("type", "EEG")
    val inlet = new LSL.StreamInlet(results(0))
    val sample = new Array[Float](inlet.info.channel_count)
    while (true) {
      inlet.pull_sample(sample)
      var k = -1
      while ({k+=1;k < sample.length}) print("\t" + sample(k))
      println()
    }
}