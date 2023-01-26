ThisBuild / version := "1.0.0"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "jfamilycounselor.core",
    assembly / assemblyOutputPath := file(
      "../jfamilycounselor/lib/jfamilycounselor.core.jar"
    ),
    libraryDependencies ++= Seq(
      "org.eclipse.jdt" % "org.eclipse.jdt.core" % "3.32.0" % "provided",
      "org.eclipse.jdt" % "org.eclipse.jdt.core.manipulation" % "1.17.0" % "provided",
      "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",
      "com.github.tototoshi" %% "scala-csv" % "1.3.10"
    )
  )