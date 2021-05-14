import cats.effect.{IO, Resource}
import com.typesafe.config.ConfigFactory
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax._

package object config {
  case class ServerConfig(host: String, port: Int)

  case class DatabaseConfig(
      host: String,
      port: Int,
      database: String,
      user: String,
      password: String,
      threadPoolSize: Int
  )

  case class Config(server: ServerConfig, database: DatabaseConfig)

  object Config {
    def load(configFile: String = "application.conf"): Resource[IO, Config] = {
      Resource.unit[IO].flatMap { blocker =>
        Resource.eval(ConfigSource.fromConfig(ConfigFactory.load(configFile)).loadF[IO, Config](blocker))
      }
    }
  }
}
