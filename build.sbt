// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `hello-zio` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .settings(settings)
    .settings(
      libraryDependencies ++= library.zio ++ library.zioConfig ++ library.zioGrpc ++
        Seq(
          library.zioSagaCore,
          library.zioStream,
          library.zioKafka,
          library.jacksonDatabind,
          library.zioPrometheus,
          library.zioPrelude,
          library.zioTest         % Test,
          library.zioTestSBT      % Test,          
          library.scalaCheck % Test,
          library.scalaTest  % Test,
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val scalaCheck = "1.15.1"
      val scalaTest  = "3.2.3"
      val zio = "1.0.3"
      val zioSagaCore = "0.2.0+7-c1504753"
      val grpcVersion = "1.34.0"
      val zioConfig = "1.0.0-RC29"
    }
    val scalaCheck = "org.scalacheck" %% "scalacheck" % Version.scalaCheck
    val scalaTest  = "org.scalatest"  %% "scalatest"  % Version.scalaTest
    val zio        = Seq("zio", "zio-test").map("dev.zio" %% _ % Version.zio)
    val zioTest    = "dev.zio"          %% "zio-test"     % Version.zio
    val zioTestSBT = "dev.zio"          %% "zio-test-sbt" % Version.zio
    val zioSagaCore = "com.vladkopanev" %% "zio-saga-core" % Version.zioSagaCore
    val zioConfig = Seq("zio-config", "zio-config-magnolia", "zio-config-typesafe").map(d=>"dev.zio" %% d % Version.zioConfig)
    val zioGrpc = Seq(
      "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
      "io.grpc" % "grpc-netty" % Version.grpcVersion
    )
    val zioStream = "dev.zio" %% "zio-streams" % "1.0.3"
    val zioKafka = "dev.zio" %% "zio-kafka"   % "0.13.0"
    val zioPrelude = "dev.zio" %% "zio-prelude"   % "1.0.0-RC1"
    val zioPrometheus = "dev.zio" %% "zio-metrics-prometheus" % "1.0.1"
    val jacksonDatabind = "com.fasterxml.jackson.core" % "jackson-databind" % "2.10.2"
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
  grpcSettings ++
  scalafmtSettings

lazy val commonSettings =
  Seq(
    // scalaVersion from .travis.yml via sbt-travisci
    scalaVersion := "2.13.4",
    organization := "io.metabookmarks",
    organizationName := "Olivier NOUGUIER",
    startYear := Some(2020),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "UTF-8",
      "-Ywarn-unused:imports",
    ),
    Compile / unmanagedSourceDirectories := Seq((Compile / scalaSource).value),
    Test / unmanagedSourceDirectories := Seq((Test / scalaSource).value),
   // Compile / compile / wartremoverWarnings ++= Warts.allBut(Wart.Any, Wart.Nothing),
   // wartremoverExcluded += sourceManaged.value
)

lazy val grpcSettings = Seq(
  PB.targets in Compile := Seq(
    scalapb.gen(grpc = true) -> (sourceManaged in Compile).value,
    scalapb.zio_grpc.ZioCodeGenerator -> (sourceManaged in Compile).value
)
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
  )
