package jp.pigumer.sbt.cloud.serverless

class Environment(val env: () ⇒ Map[String, String])

object Environment {

  def apply(env: ⇒ Map[String, String]): Environment =
    new Environment(env _)
}
