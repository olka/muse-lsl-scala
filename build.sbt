name := "Muse-lsl"

version := "0.1"


scalaVersion := "2.12.2"
libraryDependencies += "net.java.dev.jna" % "jna" % "4.2.2"

unmanagedResourceDirectories in Compile += baseDirectory.value / "resources"
includeFilter in (Compile, unmanagedResourceDirectories):= ".dll,.so,.dylib"