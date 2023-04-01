//addSbtPlugin("ch.epfl.lamp"      % "sbt-dotty"       % "0.5.3")
addSbtPlugin("com.dwijnand"      % "sbt-dynver"      % "4.1.1")
addSbtPlugin("com.dwijnand"      % "sbt-travisci"    % "1.2.0")
addSbtPlugin("de.heikoseeberger" % "sbt-header"      % "5.7.0")
addSbtPlugin("org.scalameta"     % "sbt-scalafmt"    % "2.4.6")
//addSbtPlugin("org.wartremover"   % "sbt-wartremover" % "2.4.13")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.0.0")

val zioGrpcVersion = "0.6.0-test8"
//val zioGrpcVersion = "0.6.0-test1+10-a37eeee2+20220206-1858-SNAPSHOT"

//addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.34")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.6")

libraryDependencies += "com.thesamet.scalapb.zio-grpc" %% "zio-grpc-codegen" % zioGrpcVersion
libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.12"
