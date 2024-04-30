name := "NAM-installer"

organization := "com.sc4nam"

version := "47.0.4"

javacOptions ++= Seq(
  "--release", "8",
  "-Xdoclint:-missing",  // ignore verbose warnings about missing javadoc comments
  "-encoding", "UTF-8")

crossPaths := false

autoScalaLibrary := false

Compile / mainClass := Some("installer.mainframe.InstallerTabs")

// Create a large executable jar with `sbt assembly`.
assembly / assemblyJarName := s"NAM-installer-version${version.value}.jar"

assembly / assemblyMergeStrategy := { _ => MergeStrategy.singleOrError }
