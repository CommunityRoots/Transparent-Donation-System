name := """community-roots"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  filters,
  "mysql" % "mysql-connector-java" % "5.1.18",
  "org.mindrot"  % "jbcrypt"   % "0.3m",
  "javax.mail" % "javax.mail-api" % "1.5.2",
  "com.typesafe.play" %% "play-mailer" % "2.4.0",
  "com.sun.mail" % "javax.mail" % "1.5.2",
  "com.stripe" % "stripe-java" % "1.26.0",
  "org.seleniumhq.selenium" % "selenium-java" % "2.31.0" % "test",
  "net.sourceforge.htmlunit" % "htmlunit" % "2.14" % "test"
)
