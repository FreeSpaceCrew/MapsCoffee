name := "MapsCoffee"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.2"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

libraryDependencies ++= Seq(
//  jdbc,
//  cache,
  ws,
  specs2 % Test,
  "org.specs2" %% "specs2-core" % "3.6.4" % "test",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)
//libraryDependencies +=

scalacOptions in Test ++= Seq("-Yrangepos")

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
