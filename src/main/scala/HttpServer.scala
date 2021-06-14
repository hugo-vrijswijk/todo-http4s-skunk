import cats.effect._
import com.comcast.ip4s.{Host, Port}
import config.Config
import db.Database
import natchez.Trace.Implicits.noop
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Server
import repository.TodoRepository
import service.TodoService
import skunk.Session

object HttpServer {
  def create(configFile: String = "application.conf"): IO[Unit] = {
    resources(configFile).flatMap(create).use(_ => IO.never)
  }

  private def resources(configFile: String): Resource[IO, Resources] = {
    for {
      config  <- Config.load(configFile)
      session <- Database.session(config.database)
    } yield Resources(session, config)
  }

  private def create(resources: Resources): Resource[IO, Server] = {
    for {
      _         <- Resource.eval(Database.initialize(resources.config.database))
      repository = new TodoRepository(resources.sessionResource)
      server    <- EmberServerBuilder
                     .default[IO]
                     .withPort(resources.config.server.port)
                     .withHost(resources.config.server.host)
                     .withHttpApp(new TodoService(repository).routes.orNotFound)
                     .build
    } yield server
  }

  case class Resources(sessionResource: Resource[IO, Session[IO]], config: Config)
}
