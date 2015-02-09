name := """community-roots"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "mysql" % "mysql-connector-java" % "5.1.18",
  "org.mindrot"  % "jbcrypt"   % "0.3m",
  "javax.mail" % "javax.mail-api" % "1.5.2",
  "com.typesafe.play" %% "play-mailer" % "2.4.0"
)
