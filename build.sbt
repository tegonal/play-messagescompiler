sbtPlugin := true

name := "play-messagescompiler"

organization := "com.tegonal"

version := "1.0.2"

description := "SBT plugin for compiled messages resources in Play 2.2"

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

resolvers ++= Seq(
  Resolver.url("Typesafe repository", url("http://repo.typesafe.com/typesafe/releases/"))(Resolver.ivyStylePatterns),
  "Typesafe Releases Maven " at "http://repo.typesafe.com/typesafe/releases/")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.1")

scalacOptions += "-deprecation"

initialCommands := "import com.tegonal.play-messagescompiler._"
