package jp.pigumer.sbt.cloud.aws.cloudformation

import sbt.Keys.streams
import sbt.complete.DefaultParsers.spaceDelimited
import sbt.{Def, _}

import scala.util.{Failure, Success, Try}

trait CreateOrUpdateStack extends CreateStack with UpdateStack {

  import jp.pigumer.sbt.cloud.aws.cloudformation.CloudformationPlugin.autoImport._

  def createStackTask = Def.inputTask {
    val log = streams.value.log
    val settings = awscfSettings.value
    val client = awscf.value
    spaceDelimited("<shortName>").parsed match {
      case Seq(shortName) ⇒
        (for {
          s ← Try(awscfStacks.value.values.getOrElse(shortName, sys.error(s"$shortName of the stack is not defined")))
          stack ← Try(s())
          exists ← Try {
            val list = awscfListStacks.value
            list.exists(s ⇒ s.getStackName == stack.stackName.value)
          }
          stacks ← {
            if (stack.ifExistsUpdate && exists)
              update(client, settings, stack, log)
            else
              create(client, settings, stack, log)
          }
          _ ← Try {
            stacks.foreach(s ⇒ log.info(s"${s.getStackName} ${s.getStackStatus}"))
          }
        } yield ()) match {
          case Success(_) ⇒ ()
          case Failure(t) ⇒
            log.trace(t)
            sys.error(t.getMessage)
        }
      case _ ⇒ sys.error("Usage: <shortName>")
    }
  }

  def updateStackTask = Def.inputTask {
    val log = streams.value.log
    val settings = awscfSettings.value
    val client = awscf.value
    spaceDelimited("<shortName>").parsed match {
      case Seq(shortName) ⇒
        (for {
          stack ← Try(awscfStacks.value.values.getOrElse(shortName, sys.error(s"$shortName of the stack is not defined")))
          exists ← Try {
            val list = awscfListStacks.value
            list.exists(s ⇒ s.getStackName == stack().stackName.value)
          }
          _ ←  {
            val s = stack()
            if (exists)
              update(client, settings, s, log)
            else if (s.ifExistsUpdate)
              create(client, settings, s, log)
            else
              update(client, settings, s, log)
          }
        } yield ()) match {
          case Success(_) ⇒ ()
          case Failure(t) ⇒
            log.trace(t)
            sys.error(t.getMessage)
        }
      case _ ⇒ sys.error("Usage: <shortName>")
    }
  }

}
