# This Dockerfile has two required ARGs to determine which base image
# to use for the JDK and which sbt version to install.

FROM openjdk:8

ARG SBT_VERSION=1.3.8

RUN \
  curl -L -o sbt-$SBT_VERSION.deb http://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get install sbt && \
  sbt sbtVersion

WORKDIR /HabitUser

ADD . /HabitUser

CMD sbt run