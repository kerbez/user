name := "user"

version := "1.0.0"

scalaVersion := "2.12.10"

lazy val akkaManagementVersion = "1.0.0"
val akkaVersion              = "2.5.22" //"2.6.1"
val akkaHttpVersion       = "10.1.5"
lazy val akkaHttpJson4sVersion    = "1.25.2"
val elastic4sVersion      = "7.3.1"
val json4sVersion         = "3.6.7"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.22"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.22"
libraryDependencies += "com.typesafe.akka" %% "akka-cluster-sharding" % "2.5.22"
libraryDependencies += "com.ajjpj.simple-akka-downing" %% "simple-akka-downing" % "0.9.1"
libraryDependencies += "com.typesafe.akka" %% "akka-http"   % akkaHttpVersion
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json"   % akkaHttpVersion
libraryDependencies ++= Seq(
  "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % "1.0.6",
  "de.heikoseeberger"      %% "akka-http-json4s"        % "1.30.0",
  "com.typesafe.akka" %% "akka-discovery" % "2.5.22",
  "com.lightbend.akka"         %% "akka-stream-alpakka-slick" % "1.1.2",
  "org.postgresql"       % "postgresql"         % "42.2.5",
  "com.zaxxer"           % "HikariCP"           % "2.4.5",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.4.0",
  "joda-time"            % "joda-time"          % "2.7",
  "org.joda"             % "joda-convert"       % "1.7",
//  "com.typesafe.akka"      %% "akka-slf4j"              % akkaVersion,
  "org.json4s"             %% "json4s-core"             % json4sVersion,
  "org.json4s"             %% "json4s-jackson"          % json4sVersion,
  "org.json4s"             %% "json4s-native"           % json4sVersion,
  "com.outr"               %% "hasher"                  % "1.2.2",
  "com.pauldijou"          %% "jwt-json4s-jackson"      % "4.2.0"
)

enablePlugins(JavaAppPackaging)