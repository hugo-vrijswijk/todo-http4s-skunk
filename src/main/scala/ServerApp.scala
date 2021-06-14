import cats.effect.{ExitCode, IO, IOApp}

object ServerApp extends IOApp.Simple {
  def run: IO[Unit] = {
    HttpServer.create()
  }
}
