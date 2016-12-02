name := "HW3_Github"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
 "com.typesafe.akka" % "akka-actor_2.11" % "2.4.11",

 "com.typesafe.akka" %% "akka-http-experimental" % "1.0",

 "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "1.0",

 "com.typesafe.akka" %%"akka-http-testkit-experimental" % "1.0",

 "org.scalatest" %% "scalatest" % "2.2.5" % "test",

 "de.heikoseeberger" %% "akka-http-circe" % "1.10.1",

  "com.typesafe.play" %% "play-json" % "2.3.4",

  "org.jgrapht" % "jgrapht-core" % "1.0.0",

  "junit" % "junit" % "4.12",

   "org.jgrapht" % "jgrapht-core" % "1.0.0"

)


resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.bintrayRepo("hseeberger", "maven")