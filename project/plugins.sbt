addSbtPlugin("com.dwijnand"      % "sbt-dynver"      % "4.0.0")
addSbtPlugin("com.dwijnand"      % "sbt-travisci"    % "1.2.0")
addSbtPlugin("de.heikoseeberger" % "sbt-header"      % "5.6.0")
addSbtPlugin("org.scalameta"     % "sbt-scalafmt"    % "2.4.2")
addSbtPlugin("org.wartremover"   % "sbt-wartremover" % "2.4.10")

val zioGrpcVersion = "0.4.0"

addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.34")

libraryDependencies += "com.thesamet.scalapb.zio-grpc" %% "zio-grpc-codegen" % zioGrpcVersion