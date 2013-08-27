import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "ConfLabs"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      

    // Fix for issue with running unit tests under Play 2.1.3
    // more details: http://stackoverflow.com/questions/18275638/play-framework-2-1-3-doesnt-run-any-tests
    testOptions in Test ~= { args =>
      for {
        arg <- args
        val ta: Tests.Argument = arg.asInstanceOf[Tests.Argument]
        val newArg = if(ta.framework == Some(TestFrameworks.JUnit)) ta.copy(args = List.empty[String]) else ta
      } yield newArg
    }
  )

}
