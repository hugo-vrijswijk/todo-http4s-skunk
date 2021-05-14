import cats.effect._
import config.Config
import db.Database
import natchez.Trace.Implicits.noop
import org.http4s.implicits._
import org.http4s.server.blaze._
import repository.TodoRepository
import service.TodoService
import skunk.Session

import scala.concurrent.ExecutionContext

object HttpServer {
  def create(
      configFile: String = "application.conf"
  )(implicit ec: ExecutionContext): IO[ExitCode] = {
    resources(configFile).use(create)
  }

  private def resources(configFile: String): Resource[IO, Resources] = {
    for {
      config  <- Config.load(configFile)
      session <- Database.session(config.database)
    } yield Resources(session, config)
  }

  private def create(
      resources: Resources
  )(implicit ec: ExecutionContext): IO[ExitCode] = {
    for {
      _         <- Database.initialize(resources.config.database)
      repository = new TodoRepository(resources.sessionResource)
      exitCode  <- BlazeServerBuilder[IO](ec)
                     .bindHttp(resources.config.server.port, resources.config.server.host)
                     .withHttpApp(new TodoService(repository).routes.orNotFound)
                     .serve
                     .compile
                     .lastOrError
    } yield exitCode
  }

  case class Resources(sessionResource: Resource[IO, Session[IO]], config: Config)
}
