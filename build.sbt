sbtPlugin := true

name := "play-messagescompiler"

organization := "com.tegonal"

version := "1.0.6-SNAPSHOT"

description := "SBT plugin for compiled messages resources in Play 2.2"

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

resolvers ++= Seq(
  Resolver.url("Typesafe repository", url("http://repo.typesafe.com/typesafe/releases/"))(Resolver.ivyStylePatterns),
  "Typesafe Releases Maven " at "http://repo.typesafe.com/typesafe/releases/")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.7")

libraryDependencies ++= Seq(
    "org.specs2" %% "specs2-core" % "2.4.14" % "test"
  )

scalacOptions += "-deprecation"

initialCommands := "import com.tegonal.play-messagescompiler._"
