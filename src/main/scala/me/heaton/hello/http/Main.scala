package me.heaton.hello.http

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object Main extends IOApp {
  def run(args: List[String]) =
    HelloServer.stream[IO].compile.drain.as(ExitCode.Success)
}