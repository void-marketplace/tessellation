import Dependencies.{Libraries, _}

ThisBuild / scalaVersion := "2.13.5"
ThisBuild / version := "0.0.1"
ThisBuild / organization := "org.constellation"
ThisBuild / organizationName := "tesselation"

ThisBuild / evictionErrorLevel := Level.Warn
ThisBuild / scalafixDependencies += Libraries.organizeImports

resolvers += Resolver.sonatypeRepo("snapshots")

val scalafixCommonSettings = inConfig(IntegrationTest)(scalafixConfigSettings(IntegrationTest))

lazy val commonSettings = Seq(
  scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info", "-language:reflectiveCalls"),
  scalafmtOnCompile := true,
  scalafixOnCompile := true,
  resolvers += Resolver.sonatypeRepo("snapshots")
)

lazy val commonTestSettings = Seq(
  testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
  libraryDependencies ++= Seq(
    Libraries.weaverCats,
    Libraries.weaverDiscipline,
    Libraries.weaverScalaCheck
  ).map(_ % Test)
)

ThisBuild / assemblyMergeStrategy := {
  case "logback.xml"                                       => MergeStrategy.first
  case x if x.contains("io.netty.versions.properties")     => MergeStrategy.discard
  case PathList(xs @ _*) if xs.last == "module-info.class" => MergeStrategy.first
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}

fork in Global := true
cancelable in Global := true

lazy val root = (project in file("."))
  .settings(
    name := "tesselation"
  )
  .aggregate(keytool, kernel, shared, core, testShared)

lazy val kernel = (project in file("modules/kernel"))
  .enablePlugins(AshScriptPlugin)
  .dependsOn(testShared % Test)
  .settings(
    name := "tesselation-kernel",
    Defaults.itSettings,
    scalafixCommonSettings,
    commonSettings,
    commonTestSettings,
    makeBatScripts := Seq(),
    libraryDependencies ++= Seq(
      CompilerPlugin.kindProjector,
      CompilerPlugin.semanticDB,
      Libraries.drosteCore,
      Libraries.fs2
    )
  )

lazy val keytool = (project in file("modules/keytool"))
  .enablePlugins(AshScriptPlugin)
  .dependsOn(testShared % Test)
  .settings(
    name := "tesselation-keytool",
    Defaults.itSettings,
    scalafixCommonSettings,
    commonSettings,
    commonTestSettings,
    makeBatScripts := Seq(),
    libraryDependencies ++= Seq(
      CompilerPlugin.kindProjector,
      CompilerPlugin.betterMonadicFor,
      CompilerPlugin.semanticDB,
      Libraries.bc,
      Libraries.bcExtensions,
      Libraries.cats,
      Libraries.catsEffect,
      Libraries.circeCore,
      Libraries.circeGeneric,
      Libraries.circeParser,
      Libraries.circeRefined,
      Libraries.cirisCore,
      Libraries.cirisEnum,
      Libraries.cirisRefined,
      Libraries.comcast,
      Libraries.derevoCore,
      Libraries.derevoCats,
      Libraries.derevoCirce,
      Libraries.log4cats,
      Libraries.logback % Runtime,
      Libraries.monocleCore,
      Libraries.monocleMacro,
      Libraries.newtype,
      Libraries.scopt,
      Libraries.refinedCore,
      Libraries.refinedCats
    )
  )

lazy val shared = (project in file("modules/shared"))
  .enablePlugins(AshScriptPlugin)
  .dependsOn(keytool, testShared % Test)
  .settings(
    name := "tesselation-shared",
    Defaults.itSettings,
    scalafixCommonSettings,
    commonSettings,
    commonTestSettings,
    makeBatScripts := Seq(),
    libraryDependencies ++= Seq(
      CompilerPlugin.kindProjector,
      CompilerPlugin.betterMonadicFor,
      CompilerPlugin.semanticDB,
      Libraries.cats,
      Libraries.chill,
      Libraries.circeCore,
      Libraries.circeGeneric,
      Libraries.circeParser,
      Libraries.circeRefined,
      Libraries.cirisCore,
      Libraries.cirisEnum,
      Libraries.cirisRefined,
      Libraries.comcast,
      Libraries.derevoCore,
      Libraries.derevoCats,
      Libraries.derevoCirce,
      Libraries.doobieQuill,
      Libraries.guava,
      Libraries.monocleCore,
      Libraries.newtype,
      Libraries.refinedCore,
      Libraries.refinedCats,
      Libraries.http4sCore
    )
  )

lazy val testShared = (project in file("modules/test-shared"))
  .configs(IntegrationTest)
  .settings(
    name := "tesselation-test-shared",
    Defaults.itSettings,
    scalafixCommonSettings,
    commonSettings,
    commonTestSettings,
    libraryDependencies ++= Seq(
      CompilerPlugin.kindProjector,
      CompilerPlugin.betterMonadicFor,
      CompilerPlugin.semanticDB,
      Libraries.catsLaws,
      Libraries.log4catsNoOp,
      Libraries.monocleLaw,
      Libraries.refinedScalacheck,
      Libraries.cats,
      Libraries.catsEffect,
      Libraries.circeCore,
      Libraries.circeGeneric,
      Libraries.circeParser,
      Libraries.circeRefined,
      Libraries.fs2,
      Libraries.http4sDsl,
      Libraries.http4sServer,
      Libraries.http4sClient,
      Libraries.http4sCirce,
      Libraries.http4sJwtAuth,
      Libraries.weaverCats,
      Libraries.weaverDiscipline,
      Libraries.weaverScalaCheck
    )
  )

lazy val core = (project in file("modules/core"))
  .enablePlugins(AshScriptPlugin)
  .dependsOn(keytool, kernel, shared % "compile->compile;test->test", testShared % Test)
  .settings(
    name := "tesselation-core",
    Defaults.itSettings,
    scalafixCommonSettings,
    commonSettings,
    commonTestSettings,
    makeBatScripts := Seq(),
    libraryDependencies ++= Seq(
      CompilerPlugin.kindProjector,
      CompilerPlugin.betterMonadicFor,
      CompilerPlugin.semanticDB,
      Libraries.cats,
      Libraries.catsEffect,
      Libraries.circeCore,
      Libraries.circeGeneric,
      Libraries.circeParser,
      Libraries.circeRefined,
      Libraries.cirisCore,
      Libraries.cirisEnum,
      Libraries.cirisRefined,
      Libraries.derevoCore,
      Libraries.derevoCats,
      Libraries.derevoCirce,
      Libraries.doobieCore,
      Libraries.doobieHikari,
      Libraries.doobieH2,
      Libraries.doobieQuill,
      Libraries.drosteCore,
      Libraries.drosteLaws,
      Libraries.drosteMacros,
      Libraries.fs2,
      Libraries.flyway,
      Libraries.fs2DataCsv,
      Libraries.fs2DataCsvGeneric,
      Libraries.h2,
      Libraries.http4sDsl,
      Libraries.http4sServer,
      Libraries.http4sClient,
      Libraries.http4sCirce,
      Libraries.http4sJwtAuth,
      Libraries.javaxCrypto,
      Libraries.log4cats,
      Libraries.logback % Runtime,
      Libraries.mapref,
      Libraries.monocleCore,
      Libraries.micrometerPrometheusRegistry,
      Libraries.newtype,
      Libraries.redis4catsEffects,
      Libraries.redis4catsLog4cats,
      Libraries.refinedCore,
      Libraries.refinedCats,
      Libraries.scopt,
      Libraries.skunkCore,
      Libraries.skunkCirce,
      Libraries.squants
    )
  )

addCommandAlias("runLinter", ";scalafixAll --rules OrganizeImports")
