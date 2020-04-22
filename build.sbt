name := "user"

version := "1.0.0"

scalaVersion := "2.12.7"
lazy val akkaManagementVersion = "1.0.0"
lazy val akkaVersion              = "2.5.27" //"2.6.3"
lazy val akkaHttpJson4sVersion    = "1.25.2"

libraryDependencies += "com.typesafe.akka" %% "akka-http"   % "10.1.5"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json"   % "10.1.5"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.5.22"
libraryDependencies += "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion
libraryDependencies += "com.ajjpj.simple-akka-downing" %% "simple-akka-downing" % "0.9.1"
libraryDependencies ++= Seq(
  "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % "1.0.6",
  "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
  "com.typesafe.akka"             %% "akka-persistence"                    % akkaVersion,
  "com.typesafe.akka"             %% "akka-persistence-query"              % akkaVersion,
  "com.typesafe.akka"             %% "akka-persistence-cassandra"          % "0.98",
  "com.typesafe.akka"             %% "akka-persistence-cassandra-launcher" % "0.98" % "test"
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