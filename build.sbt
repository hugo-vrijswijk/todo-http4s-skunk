lazy val commonSettings = Seq(
  name := "todo http4s skunk",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.13.5",
  scalacOptions ++= Seq(
    "-deprecation",
    "-Xfatal-warnings",
    "-Ywarn-value-discard",
    "-Xlint:missing-interpolator"
  )
)

lazy val Http4sVersion = "0.21.22"

lazy val SkunkVersion = "0.0.18"

// Needed for flyway migrations
lazy val JdbcDriver = "42.2.20"

lazy val FlywayVersion = "7.5.2"

lazy val CirceVersion = "0.13.0"

lazy val PureConfigVersion = "0.13.0"

lazy val LogbackVersion = "1.2.3"

lazy val ScalaTestVersion = "3.2.9"

lazy val ScalaMockVersion = "5.1.0"

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      "org.http4s"            %% "http4s-blaze-server"    % Http4sVersion,
      "org.http4s"            %% "http4s-circe"           % Http4sVersion,
      "org.http4s"            %% "http4s-dsl"             % Http4sVersion,
      "org.http4s"            %% "http4s-blaze-client"    % Http4sVersion    % "it,test",
      "org.tpolecat"          %% "skunk-core"             % SkunkVersion,
      "org.postgresql"         % "postgresql"             % JdbcDriver,
      "org.flywaydb"           % "flyway-core"            % FlywayVersion,
      "io.circe"              %% "circe-generic"          % CirceVersion,
      "io.circe"              %% "circe-literal"          % CirceVersion     % "it,test",
      "io.circe"              %% "circe-optics"           % CirceVersion     % "it",
      "com.github.pureconfig" %% "pureconfig"             % PureConfigVersion,
      "com.github.pureconfig" %% "pureconfig-cats-effect" % PureConfigVersion,
      "ch.qos.logback"         % "logback-classic"        % LogbackVersion,
      "org.scalatest"         %% "scalatest"              % ScalaTestVersion % "it,test",
      "org.scalamock"         %% "scalamock"              % ScalaMockVersion % "test"
    )
  )
