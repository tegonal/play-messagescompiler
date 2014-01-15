sbtPlugin := true

name := "play-messagescompiler"

organization := "com.tegonal"

version := "1.0.0"

description := "SBT plugin for compiled messages resources in Play 2.2"

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)

resolvers += "Tegonal releases" at "https://github.com/tegonal/tegonal-mvn/raw/master/releases/"

resolvers ++= Seq(
  Resolver.url("Typesafe repository", url("http://repo.typesafe.com/typesafe/releases/"))(Resolver.ivyStylePatterns),
  "Typesafe Releases Maven " at "http://repo.typesafe.com/typesafe/releases/")

libraryDependencies ++= Seq(
  "com.tegonal" %% "resourceparser" % "1.0.0"
)

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.1")

initialCommands := "import com.tegonal.play-messagescompiler._"
