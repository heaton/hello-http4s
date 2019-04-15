package me.heaton.hello.http

import cats.effect._
import cats.implicits._
import org.http4s.{EntityEncoder, HttpRoutes, Response}
import org.http4s.syntax._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze._
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._

object HelloHttp4s extends IOApp {
  val helloWorldService = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name => Ok(s"Hello $name.")
  }

  val repository = new HelloDoobie

  val countryService: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "countries" / "popular" => repository.popularCountries.flatMap(Ok(_))
    case GET -> Root / "country" / code => repository.findCountry(code).flatMap(okOrNotFound(_))
  }

  private def okOrNotFound[T](option: Option[T])(implicit entityEncoder: EntityEncoder[IO, T]): IO[Response[IO]] = option match {
    case Some(v) => Ok(v)
    case _ => NotFound()
  }

  override def run(args: List[String]): IO[ExitCode] = {
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp((helloWorldService <+> countryService).orNotFound)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }

}
