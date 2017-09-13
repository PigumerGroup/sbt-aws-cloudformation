package jp.pigumer.sbt.cloud.aws.ecr

object AwsecrCommands {

  def tag(dockerPath: String, sourceImage: String, targetImage: String) = {
    val l = dockerPath :: "tag" :: sourceImage :: targetImage :: Nil
    val cmd = l.mkString(" ")
    sys.process.Process(cmd)! match {
      case 0 ⇒ cmd
      case _ ⇒ throw new RuntimeException("failed tag command")
    }
  }

  def push(dockerPath: String, image: String) = {
    val l = dockerPath :: "push" :: image :: Nil
    val cmd = l.mkString(" ")
    sys.process.Process(cmd)! match {
      case 0 ⇒ cmd
      case _ ⇒ throw new RuntimeException("failed push command")
    }
  }

}
