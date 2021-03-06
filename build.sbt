name := "user"

version := "1.0.0"

scalaVersion := "2.12.7"


lazy val akkaManagementVersion = "1.0.0"
lazy val akkaVersion              = "2.6.1" //"2.6.3"
val akkaHttpVersion       = "10.1.11"
lazy val akkaHttpJson4sVersion    = "1.25.2"
val elastic4sVersion      = "7.3.1"
val json4sVersion         = "3.6.7"

libraryDependencies += "com.typesafe.akka" %% "akka-http"   % akkaHttpVersion
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json"   % akkaHttpVersion
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion
libraryDependencies += "com.ajjpj.simple-akka-downing" %% "simple-akka-downing" % "0.9.1"
libraryDependencies ++= Seq(
  "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % "1.0.6",
  "de.heikoseeberger"      %% "akka-http-json4s"        % "1.30.0",
  "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
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
//libraryDependencies += "com.lightbend.akka.management" %% "akka-management"                     % akkaManagementVersion
//libraryDependencies +="com.lightbend.akka.management" %% "akka-management-cluster-bootstrap"   % akkaManagementVersion
//libraryDependencies +="com.lightbend.akka.management" %% "akka-management-cluster-http"        % akkaManagementVersion
//libraryDependencies +="com.lightbend.akka.discovery"  %% "akka-discovery-kubernetes-api"       % akkaManagementVersion
//  libraryDependencies ++= Seq(
//  "com.lightbend.akka.management" %% "akka-management"                     % akkaManagementVersion,
//  "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap"   % akkaManagementVersion,
//  "com.lightbend.akka.management" %% "akka-management-cluster-http"        % akkaManagementVersion,
//  "com.lightbend.akka.discovery"  %% "akka-discovery-kubernetes-api"       % akkaManagementVersion
//)

//
//libraryDependencies ++= Seq(
//  "com.lightbend.akka.management" %% "akka-management"                     % akkaManagementVersion,
//  "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap"   % akkaManagementVersion,
//  "com.lightbend.akka.management" %% "akka-management-cluster-http"        % akkaManagementVersion,
//  "com.lightbend.akka.discovery"  %% "akka-discovery-kubernetes-api"       % akkaManagementVersion,
//  "com.typesafe.akka"             %% "akka-cluster"                        % akkaVersion,
//  "com.typesafe.akka"             %% "akka-cluster-tools"                  % akkaVersion,
//  "com.typesafe.akka"             %% "akka-cluster-sharding"               % akkaVersion,
//  "com.typesafe.akka"             %% "akka-persistence"                    % akkaVersion,
//  "com.typesafe.akka"             %% "akka-persistence-query"              % akkaVersion,
//  "com.typesafe.akka"             %% "akka-multi-node-testkit"             % akkaVersion,
//  "com.typesafe.akka"             %% "akka-slf4j"                          % akkaVersion,
//  "com.typesafe.akka"             %% "akka-persistence-cassandra"          % "0.98",
//  "com.typesafe.akka"             %% "akka-persistence-cassandra-launcher" % "0.98" % "test",
//  "org.iq80.leveldb"              % "leveldb"                              % "0.7" % "test",
//  "org.fusesource.leveldbjni"     % "leveldbjni-all"                       % "1.8" % "test",
//  "ch.qos.logback"                % "logback-classic"                      % "1.2.3",
//  "org.apache.commons"            % "commons-lang3"                        % "3.6",
//  "org.scalatest"                 %% "scalatest"                           % "3.0.4" % "test",
//  "commons-io"                    % "commons-io"                           % "2.4" % "test",
//  "de.heikoseeberger"             %% "akka-http-json4s"                    % akkaHttpJson4sVersion,
//  "commons-io"                    % "commons-io"                           % "2.6"
//)

enablePlugins(JavaAppPackaging)