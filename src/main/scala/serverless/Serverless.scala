package serverless

import sbt.complete.DefaultParsers.spaceDelimited
import sbt._
import sbt.Keys._

object Serverless {
  import cloudformation.CloudformationPlugin.autoImport._

  def serverlessExec = Def.inputTask {
    val logger = streams.value.log
    val workingDirectory = serverlessWorkingDirectory.value
    val cmdline = serverless.value +: spaceDelimited("<args>").parsed
    val cmd = cmdline.mkString(" ")
    logger.info(cmd)
    sys.process.Process(cmd, workingDirectory)! match {
      case 0 ⇒ ()
      case _ ⇒ sys.error(s"Command failed: $cmd")
    }
  }
}
