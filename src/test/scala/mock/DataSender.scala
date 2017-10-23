package mock

import edu.ucsd.sccn.LSL

object DataSender {

  def sendData(iterations:Int): Int ={
    println("Creating a new StreamInfo...")
    val info = new LSL.StreamInfo("BioSemi", "EEG", 5, 100, LSL.ChannelFormat.float32, "myuid324457")
    System.out.println("Creating an outlet...")
    val outlet = new LSL.StreamOutlet(info)
    System.out.println("Sending data...")
    val sample = new Array[Float](8)
    var t = 0
    while (t < iterations) {
      var k = -1
      while ({k=k+1;k < 5}){
        sample(k) = Math.random.toFloat * 50 - 25
      }
      outlet.push_sample(sample)
      Thread.sleep(10)
      t += 1
    }
    outlet.close()
    info.destroy()
    0
  }

  def main(args: Array[String]): Unit = {
    sendData(1000000)
  }

}
