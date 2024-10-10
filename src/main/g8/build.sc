import mill._
import mill.scalalib._
import \$ivy.`com.goyeau::mill-scalafix::0.4.2`
import com.goyeau.mill.scalafix.ScalafixModule
import scalafmt._

object config {
  val scalaVersion = "3.5.1"
}

trait AppScalaModule extends ScalaModule with ScalafixModule with ScalafmtModule {
  def scalaVersion  = config.scalaVersion
  def scalacOptions = Seq("-Wunused:all")
}

object $name$ extends AppScalaModule {
  def scalaVersion = "3.5.1"
  def ivyDeps      = Agg(
    ivy"io.github.michalliss::foxxy-backend:0.0.5",
    ivy"io.github.michalliss::foxxy-repo:0.0.5",
    ivy"dev.zio::zio-logging:2.3.1",
    ivy"dev.zio::zio-logging-slf4j2-bridge:2.3.1"
  )

  object test extends ScalaTests with TestModule.ZioTest {
    def ivyDeps = Agg(
      ivy"io.github.michalliss::foxxy-testing:0.0.5",
      ivy"dev.zio::zio-test:2.1.10",
      ivy"dev.zio::zio-test-sbt:2.1.10",
      ivy"dev.zio::zio-test-magnolia:2.1.10"
    )
  }
}
