import sbt._
import Keys._
import play.Project._
import scala._

object ApplicationBuild extends Build {

  val appName         = "elevator"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm
    //"com.etaty.rediscala" %% "rediscala" % "1.2"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    resolvers += "rediscala" at "https://github.com/etaty/rediscala-mvn/raw/master/releases/"
  )

}
