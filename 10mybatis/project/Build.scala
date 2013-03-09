import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "MyApp10"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    "org.mybatis.scala" % "mybatis-scala-core" % "1.0.0"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
      scalacOptions ++= Seq("-language:postfixOps", "-encoding", "UTF-8")
  )

}
