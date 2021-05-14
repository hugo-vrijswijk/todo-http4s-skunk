package db

import cats.effect.{IO, Resource}
import config.DatabaseConfig
import natchez.Trace
import org.flywaydb.core.Flyway
import skunk.Session

object Database {
  def session(config: DatabaseConfig)(implicit
      trace: Trace[IO]
  ): Resource[IO, Resource[IO, Session[IO]]] = {
    Session.pooled[IO](
      host = config.host,
      port = config.port,
      database = config.database,
      user = config.user,
      password = Some(config.password),
      max = config.threadPoolSize
    )
  }

  def initialize(config: DatabaseConfig): IO[Unit] = {
    IO {
      val url = s"jdbc:postgresql://${config.host}:${config.port}/${config.database}"

      val flyWay = Flyway.configure().dataSource(url, config.user, config.password).load()
      flyWay.migrate()
      ()
    }
  }
}
