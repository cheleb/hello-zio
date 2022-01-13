// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `hello-zio` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .settings(settings)
    .settings(
      libraryDependencies ++= library.zioConfig ++ library.zioGrpc ++
        Seq(
          library.zio,
          library.zioSagaCore,
          library.zioStream,
      //    library.zioKafka,
          library.jacksonDatabind,
//          library.zioPrometheus,
          library.zioPrelude,
          library.zioSchema,
          library.zhttp,
          library.zioJson,
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
      val scalaCheck = "1.15.4"
      val scalaTest  = "3.2.10"
      val zio = "2.0.0-RC1"
      val zhttp = "2.0.0-RC1"
      val zioSagaCore = "0.2.0+7-c1504753"
      val grpcVersion = "1.43.2"
      val zioConfig = "2.0.0"
      val zioSchema = "0.1.7"
      val zioJson = "0.2.0-M2"

    }
    val scalaCheck = "org.scalacheck" %% "scalacheck" % Version.scalaCheck
    val scalaTest  = "org.scalatest"  %% "scalatest"  % Version.scalaTest
    val zio        = "dev.zio"        %% "zio"        % Version.zio
    val zhttp      = "io.d11"         %% "zhttp"      % Version.zhttp
    val zioTest    = "dev.zio"          %% "zio-test"     % Version.zio
    val zioTestSBT = "dev.zio"          %% "zio-test-sbt" % Version.zio
    val zioSagaCore = "com.vladkopanev" %% "zio-saga-core" % Version.zioSagaCore
    val zioConfig = Seq("zio-config", "zio-config-magnolia", "zio-config-typesafe").map(d=>"dev.zio" %% d % Version.zioConfig)
    val zioGrpc = Seq(
      "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
      "io.grpc" % "grpc-netty" % Version.grpcVersion
    )
    val zioStream = "dev.zio" %% "zio-streams" % Version.zio
    val zioKafka = "dev.zio" %% "zio-kafka"   % "0.17.0"
    val zioPrelude = "dev.zio" %% "zio-prelude"   % "1.0.0-RC1"
    val zioSchema = "dev.zio" %% "zio-schema" % Version.zioSchema
    val zioJson = "dev.zio" %% "zio-json" % Version.zioJson
    val zioPrometheus = "dev.zio" %% "zio-metrics-prometheus" % "1.0.13"
    val jacksonDatabind = "com.fasterxml.jackson.core" % "jackson-databind" % "2.13.0"
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
     scalaVersion := "2.13.7",
    //scalaVersion := "3.0.0-RC1",
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
  Compile / PB.targets  := Seq(
    scalapb.gen(grpc = true) -> (Compile / sourceManaged).value,
    scalapb.zio_grpc.ZioCodeGenerator -> (Compile / sourceManaged).value
)
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
  )
