import $ivy.`io.chris-kipp::mill-giter8::0.2.7`
import $ivy.`com.goyeau::mill-scalafix::0.4.2`
import com.goyeau.mill.scalafix.ScalafixModule

import io.kipp.mill.giter8.G8Module
import mill._
import mill.scalalib._

object g8 extends G8Module {
  override def validationTargets =
    Seq(
      "example.compile",
      "example.test",
      "example.fix",
      "example.reformat"
    )
}

object Steward extends ScalaModule {
  def scalaVersion = "3.5.2"
  def ivyDeps = Agg(
    ivy"dev.zio::zio-logging:2.4.0",
    ivy"dev.zio::zio-logging-slf4j2-bridge:2.4.0",
    ivy"dev.zio::zio-test:2.1.12",
    ivy"dev.zio::zio-test-sbt:2.1.12",
    ivy"dev.zio::zio-test-magnolia:2.1.12",
    ivy"io.github.michalliss::foxxy-backend:0.0.7",
    ivy"io.github.michalliss::foxxy-repo:0.0.7",
    ivy"io.github.michalliss::foxxy-testing:0.0.7",
  )
}
