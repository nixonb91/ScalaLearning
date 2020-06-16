lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.13.1"
    )),
    name := "scalatest-example"
  )

scalacOptions := Seq("-unchecked", "-deprecation")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1" % Test
libraryDependencies += "org.scalactic" %% "scalactic" % "3.1.1"
libraryDependencies += "com.github.t3hnar" %% "scala-bcrypt" % "4.1"

libraryDependencies ++= Seq(
  "ch.qos.logback"  %  "logback-classic"   % "1.2.3",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.9.0",
  "io.jvm.uuid" %% "scala-uuid" % "0.3.1"
)