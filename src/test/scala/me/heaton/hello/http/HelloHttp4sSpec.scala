package me.heaton.hello.http

import cats.effect.IO
import org.http4s.Method.GET
import org.http4s._
import org.http4s.implicits._
import org.specs2.mutable.Specification

class HelloHttp4sSpec extends Specification {

  "Hello Http4s" >> {
    import HelloHttp4s._
    "have /hello" >> {
      val request = Request[IO](GET, Uri.uri("/hello/World"))
      val response = helloWorldService.orNotFound(request).unsafeRunSync()
      response.status === Status.Ok
      response.as[String].unsafeRunSync() === "\"Hello World.\""
    }

    import io.circe.generic.auto._
    import org.http4s.circe.CirceEntityCodec._

    "have country Australia" >> {
      val request = Request[IO](GET, Uri.uri("/country/AUS"))
      val response = countryService.orNotFound(request).unsafeRunSync()
      response.status === Status.Ok
      //      response.as[String].unsafeRunSync() === """{"id":1,"message":"message 1"}"""
      response.as[Country].unsafeRunSync() === Country("AUS", "Australia")
    }

    "not have country unknown" >> {
      val request = Request[IO](GET, Uri.uri("/country/unknown"))
      val response = countryService.orNotFound(request).unsafeRunSync()
      response.status === Status.NotFound
    }

    "have popular countries" >> {
      val request = Request[IO](GET, Uri.uri("/countries/popular"))
      val response = countryService.orNotFound(request).unsafeRunSync()
      response.status === Status.Ok
      response.as[List[Country]].unsafeRunSync().map(_.code) === List("BRA", "IDN", "IND", "CHN", "PAK", "USA")
    }
  }
}
