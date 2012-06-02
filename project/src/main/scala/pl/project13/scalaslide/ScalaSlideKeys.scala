package pl.project13.scalaslide

import sbt._
import sbt.InputKey
import sbt._

trait ScalaSlideKeys {

  val ScalaSlide = config("scalaslide")

  val extractTestsTask = TaskKey[Unit](
    "extract-tests",
    "Extracts test code from the presentation files, such extracted tests may be run with test:test"
  )

  val cleanTask = TaskKey[Unit](
    "clean",
    "Clean generated tests and presentation files"
  )

  val genTask = InputKey[Unit](
    "gen",
    "Generates the presentation html and pdf versions"
  )
}

object ScalaSlideKeys extends ScalaSlideKeys
