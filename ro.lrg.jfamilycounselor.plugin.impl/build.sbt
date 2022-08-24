ThisBuild / version := "1.0.0"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "ro.lrg.jfamilycounselor.plugin.impl",
    assembly / assemblyOutputPath := file(
      "../ro.lrg.jfamilycounselor.plugin/lib/ro.lrg.jfamilycounselor.plugin.impl.jar"
    ),
    libraryDependencies ++= Seq(
      "org.eclipse.jdt" % "org.eclipse.jdt.core" % "3.30.0" % "provided",
      "org.eclipse.jdt" % "org.eclipse.jdt.core.manipulation" % "1.16.100" % "provided",
      "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",
      "com.github.tototoshi" %% "scala-csv" % "1.3.10"
    )
  )