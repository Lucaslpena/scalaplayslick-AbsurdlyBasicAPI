name := "AuctionHouse"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "org.webjars" %% "webjars-play" % "2.2.2",
  "com.typesafe.play" %% "play-slick" % "0.8.0"
)

fork in Test := false

lazy val root = (project in file(".")).enablePlugins(PlayScala)