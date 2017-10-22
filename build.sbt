name := "Muse-lsl"

version := "0.1"


scalaVersion := "2.12.2"

libraryDependencies ++= {
  val akkaV       = "2.5.6"
  val akkaHttpV   = "10.0.10"
  val scalaTestV  = "3.0.4"

  Seq(
    "net.java.dev.jna" % "jna" % "4.2.2",
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
//    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV,
    "org.scalatest"     %% "scalatest" % scalaTestV % "test",
//    "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.2.2" % "test",
//    "io.gatling"            % "gatling-test-framework"    % "2.2.2" % "test"
  )
}


unmanagedResourceDirectories in Compile += baseDirectory.value / "resources"
includeFilter in (Compile, unmanagedResourceDirectories):= ".dll,.so,.dylib"