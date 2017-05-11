publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

sonatypeProfileName := "com.pigumer.sbt.cloud"

pomExtra := (
  <url>https://github.com/PigumerGroup/sbt-aws-cloudformation</url>
    <licenses>
      <license>
        <name>MIT</name>
        <url>https://opensource.org/licenses/MIT</url>
      </license>
    </licenses>
    <scm>
      <url>https://github.com/PigumerGroup/sbt-aws-cloudformation</url>
      <connection>https://github.com/PigumerGroup/sbt-aws-cloudformation.git</connection>
    </scm>
    <developers>
      <developer>
        <id>takesection</id>
        <name>Shigeki Shoji</name>
        <url>https://github.com/takesection</url>
      </developer>
    </developers>)
