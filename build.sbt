name := "user"

version := "1.0.0"

scalaVersion := "2.12.7"
lazy val akkaManagementVersion = "1.0.0"
lazy val akkaVersion              = "2.5.27" //"2.6.3"
lazy val akkaHttpJson4sVersion    = "1.25.2"

libraryDependencies += "com.typesafe.akka" %% "akka-http"   % "10.1.11"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json"   % "10.1.11"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-persistence" % akkaVersion
//libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.6.3"
libraryDependencies += "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion
libraryDependencies += "com.ajjpj.simple-akka-downing" %% "simple-akka-downing" % "0.9.1"
libraryDependencies ++= Seq(
  "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % "1.0.6",
  "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
  "com.lightbend.akka"         %% "akka-stream-alpakka-slick" % "1.1.2",
  "org.postgresql"       % "postgresql"         % "42.2.5",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.4.0",
  "com.outr"               %% "hasher"                  % "1.2.2",
  "com.pauldijou"          %% "jwt-json4s-jackson"      % "4.2.0"
)
