//addSbtPlugin("ch.epfl.lamp"      % "sbt-dotty"       % "0.5.3")
addSbtPlugin("com.dwijnand"      % "sbt-dynver"      % "4.1.1")
addSbtPlugin("com.dwijnand"      % "sbt-travisci"    % "1.2.0")
addSbtPlugin("de.heikoseeberger" % "sbt-header"      % "5.6.0")
addSbtPlugin("org.scalameta"     % "sbt-scalafmt"    % "2.4.3")
//addSbtPlugin("org.wartremover"   % "sbt-wartremover" % "2.4.13")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.8.2")

val zioGrpcVersion = "0.5.1"

//addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.34")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.3")

libraryDependencies += "com.thesamet.scalapb.zio-grpc" %% "zio-grpc-codegen" % zioGrpcVersion
//libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.1"
