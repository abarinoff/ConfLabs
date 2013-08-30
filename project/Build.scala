import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "ConfLabs"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "be.objectify" %% "deadbolt-java" % "2.1-RC2",
    "com.feth" %% "play-authenticate" % "0.3.0-SNAPSHOT",
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    javaCore,
    javaJdbc,
    javaEbean
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Play authenticate resolvers

    resolvers += Resolver.url("Objectify Play Repository (release)", url("http://schaloner.github.com/releases/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("Objectify Play Repository (snapshot)", url("http://schaloner.github.com/snapshots/"))(Resolver.ivyStylePatterns),

    resolvers += Resolver.url("play-easymail (release)", url("http://joscha.github.com/play-easymail/repo/releases/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("play-easymail (snapshot)", url("http://joscha.github.com/play-easymail/repo/snapshots/"))(Resolver.ivyStylePatterns),

    resolvers += Resolver.url("play-authenticate (release)", url("http://joscha.github.com/play-authenticate/repo/releases/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("play-authenticate (snapshot)", url("http://joscha.github.com/play-authenticate/repo/snapshots/"))(Resolver.ivyStylePatterns),

    // Fix for issue with running unit tests under Play 2.1.3
    // more details: http://stackoverflow.com/questions/18275638/play-framework-2-1-3-doesnt-run-any-tests
    testOptions in Test ~= {
      args =>
        for {
          arg <- args
          val ta: Tests.Argument = arg.asInstanceOf[Tests.Argument]
          val newArg = if (ta.framework == Some(TestFrameworks.JUnit)) ta.copy(args = List.empty[String]) else ta
        } yield newArg
    }
  )

}
