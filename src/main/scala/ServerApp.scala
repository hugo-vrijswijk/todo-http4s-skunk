import cats.effect.{ExitCode, IO, IOApp}

import scala.concurrent.ExecutionContext.Implicits.global

object ServerApp extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    HttpServer.create()
  }
}
