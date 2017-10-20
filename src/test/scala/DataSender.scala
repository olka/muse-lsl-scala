import edu.ucsd.sccn.LSL


object DataSender extends App {
    System.out.println("Creating a new StreamInfo...")
    val info = new LSL.StreamInfo("BioSemi", "EEG", 8, 100, LSL.ChannelFormat.float32, "myuid324457")
    System.out.println("Creating an outlet...")
    val outlet = new LSL.StreamOutlet(info)
    System.out.println("Sending data...")
    val sample = new Array[Float](8)
    var t = 0
    while (t < 100000) {
      var k = 0
      while (k < 8) {
        sample(k) = Math.random.toFloat * 50 - 25
        k += 1
      }
      outlet.push_sample(sample)
      Thread.sleep(10)
        t += 1
    }
    outlet.close()
    info.destroy()
}