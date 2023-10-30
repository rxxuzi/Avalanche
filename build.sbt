ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "Avalanche"
  )

// JavaFX dependencies
libraryDependencies ++= Seq(
  "org.openjfx" % "javafx-controls" % "17",
  "org.openjfx" % "javafx-fxml" % "17",
  "org.openjfx" % "javafx-web" % "17"
)

// Ensure that the JavaFX jars are on the runtime classpath
// This is necessary because JavaFX is no longer included with the JDK as of JDK 11
run / fork := true
run / javaOptions += "--module-path " + System.getenv("PATH_TO_FX")
run / javaOptions += "--add-modules=javafx.controls,javafx.fxml,javafx.web"