package me.heaton.hello.http

import doobie._
import doobie.implicits._

import cats._
import cats.effect._
import cats.implicits._

class HelloDoobie(implicit contextShift: ContextShift[IO]){

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", // driver classname
    "jdbc:postgresql:world", // connect URL (driver-specific)
    "postgres",              // user
    ""                       // password
  )

  def popularCountries: IO[List[Country]] = sql"select * from country where population > 150000000".query[Country].to[List].transact(xa)

  def findCountry(code: String): IO[Option[Country]] = sql"select * from country where code = $code".query[Country].option.transact(xa)
}
