

inThisBuild(Seq(
  scalaVersion := "2.13.8",
  run / fork := true
))

// *****************************************************************************
// Projects
// *****************************************************************************

// inThisBuild(
//   Seq(
//     run / javaOptions += "-agentpath:/Applications/YourKit-Java-Profiler-2022.3.app/Contents/Resources/bin/mac/libyjpagent.dylib",
//     run / fork := true,
//     connectInput in run := true
//   )
// )

lazy val `hello-zio` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .settings(settings)
    .settings(addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full),
      scalacOptions += "-P:kind-projector:underscore-placeholders",
)
    .settings(
      libraryDependencies ++= library.zioConfig ++ library.zioGrpc ++ library.zioSchema ++
        Seq(
          library.sttpTapirClient,
          library.zio,
          library.zioManaged,
          library.zioSagaCore,
          library.zioStream,
      //    library.zioKafka,
          library.jacksonDatabind,
//          library.zioPrometheus,
          library.zioPrelude,
          library.zhttp,
          library.zioJson,
          library.logback,
          library.zioTest         % Test,
          library.zioTestSBT      % Test
      )
    )

lazy val `zio-grpc` = project.in(file("grpc"))
  .settings(commonSettings)
  .settings(
   libraryDependencies ++= library.zioGrpc
   )
   .settings(grpcSettings)

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val zio = "2.0.0"
      val zhttp = "2.0.0-RC9"
      val zioSagaCore = "0.4.0"
      val grpcVersion = "1.47.0"
      val zioConfig = "3.0.0-RC9"
      val zioSchema = "0.2.0-RC6-2"
      val zioJson = "0.3.0-RC9"
      val zioPrelude = "1.0.0-RC15"
      val logback = "1.2.11"

    }
    val logback = "ch.qos.logback" % "logback-classic" % Version.logback
    val sttpTapirClient =   "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % "3.6.2"
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
    val zioManaged = "dev.zio" %% "zio-managed" % Version.zio
    val zioKafka = "dev.zio" %% "zio-kafka"   % "2.0.0-M3"
    val zioPrelude = "dev.zio" %% "zio-prelude"   % Version.zioPrelude
    val zioSchema = Seq("zio-schema", "zio-schema-derivation").map("dev.zio" %% _  % Version.zioSchema)
    val zioJson = "dev.zio" %% "zio-json" % Version.zioJson
    val zioPrometheus = "dev.zio" %% "zio-metrics-prometheus" % "2.0.0-RC6"
    val jacksonDatabind = "com.fasterxml.jackson.core" % "jackson-databind" % "2.13.3"
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
     scalaVersion := "2.13.8",
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
