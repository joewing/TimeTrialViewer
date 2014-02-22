scalaVersion := "2.10.3"

scalacOptions ++= Seq("-unchecked", "-deprecation")

fork := true

artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
   "scalapipe." + artifact.extension
}

libraryDependencies += "org.scala-lang" % "scala-swing" % "2.10.3"
