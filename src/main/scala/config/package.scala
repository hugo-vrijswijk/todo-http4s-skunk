import cats.effect.{IO, Resource}
import com.comcast.ip4s.{Host, Port}
import com.typesafe.config.ConfigFactory
import pureconfig._
import pureconfig.error.CannotConvert
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax._

package object config {
  case class ServerConfig(host: Host, port: Port)

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
    def load(configFile: String = "application.conf"): Resource[IO, Config] =
      Resource.eval(ConfigSource.fromConfig(ConfigFactory.load(configFile)).loadF[IO, Config]())
  }

  implicit private def hostConfigReader: ConfigReader[Host] =
    ConfigReader[String].emap(s => Host.fromString(s).toRight(CannotConvert(s, "host", "")))
  implicit private def portConfigReader: ConfigReader[Port] =
    ConfigReader[Int].emap(i => Port.fromInt(i).toRight(CannotConvert(i.toString(), "port", "")))
}
