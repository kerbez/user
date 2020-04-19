name := "user"

version := "1.0.0"

scalaVersion := "2.13.1"
lazy val akkaManagementVersion = "1.0.0"
lazy val akkaVersion              = "2.5.22"
lazy val akkaHttpJson4sVersion    = "1.25.2"

libraryDependencies += "com.typesafe.akka" %% "akka-http"   % "10.1.11"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json"   % "10.1.11"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.3"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.3"
libraryDependencies += "com.typesafe.akka" %% "akka-persistence" % "2.6.3"
//libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.6.3"
libraryDependencies += "com.typesafe.akka" %% "akka-cluster-sharding" % "2.6.3"
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