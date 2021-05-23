lazy val commonSettings = Seq(
  name := "todo http4s skunk",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.13.5",
  scalacOptions ++= Seq(
    "-deprecation",
    "-Xfatal-warnings"
  )
)

lazy val Http4sVersion = "1.0.0-M21"

lazy val SkunkVersion = "0.1.2"

// Needed for flyway migrations
lazy val JdbcDriver = "42.2.20"

lazy val FlywayVersion = "7.9.1"

lazy val CirceVersion = "0.14.0-M7"

lazy val PureConfigVersion = "0.15.0"

lazy val CatsEffectVersion = "3.1.1"

lazy val Fs2Version = "3.0.3"

lazy val LogbackVersion = "1.2.3"

lazy val ScalaTestVersion = "3.2.9"

lazy val ScalaMockVersion = "5.1.0"

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      "org.typelevel"         %% "cats-effect"            % CatsEffectVersion,
      "co.fs2"                %% "fs2-core"               % Fs2Version,
      "org.http4s"            %% "http4s-blaze-server"    % Http4sVersion,
      "org.http4s"            %% "http4s-circe"           % Http4sVersion,
      "org.http4s"            %% "http4s-dsl"             % Http4sVersion,
      "org.http4s"            %% "http4s-blaze-client"    % Http4sVersion    % "it,test",
      "org.tpolecat"          %% "skunk-core"             % SkunkVersion,
      "org.postgresql"         % "postgresql"             % JdbcDriver,
      "org.flywaydb"           % "flyway-core"            % FlywayVersion,
      "io.circe"              %% "circe-generic"          % CirceVersion,
      "io.circe"              %% "circe-literal"          % CirceVersion     % "it,test",
      "com.github.pureconfig" %% "pureconfig"             % PureConfigVersion,
      "com.github.pureconfig" %% "pureconfig-cats-effect" % PureConfigVersion,
      "ch.qos.logback"         % "logback-classic"        % LogbackVersion,
      "org.scalatest"         %% "scalatest"              % ScalaTestVersion % "it,test",
      "org.scalamock"         %% "scalamock"              % ScalaMockVersion % "test"
    )
  )
