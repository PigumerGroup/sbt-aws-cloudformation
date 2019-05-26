package jp.pigumer.sbt.cloud.aws.cloudformation

import com.amazonaws.services.cloudformation.AmazonCloudFormation
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest
import sbt.Keys.streams
import sbt.complete.DefaultParsers.spaceDelimited
import sbt.{Def, _}
import scala.util.{Failure, Success, Try}

trait CreateOrUpdateStack extends CreateStack with UpdateStack {

  import jp.pigumer.sbt.cloud.aws.cloudformation.CloudformationPlugin.autoImport._

  private def existsStack(client: AmazonCloudFormation, stackName: String, log: Logger) = {
    import scala.collection.JavaConverters._

    val req = new DescribeStacksRequest().withStackName(stackName)

    val exists = Try {
      client.describeStacks(req)
    } match {
      case Success(res) ⇒ res.getStacks.asScala.nonEmpty
      case Failure(_) ⇒ false
    }
    log.info(s"""$stackName ${if (exists) "is found" else "is not found"}""")
    exists
  }

  def createStackTask = Def.inputTask {
    val log = streams.value.log
    val settings = awscfSettings.value
    val client = awscf.value
    spaceDelimited("<shortName>").parsed match {
      case Seq(shortName) ⇒
        (for {
          s ← Try(awscfStacks.value.values.getOrElse(shortName, sys.error(s"$shortName of the stack is not defined")))
          stack ← Try(s())
          stacks ← create(client, settings, stack, log)
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
          s ← Try(awscfStacks.value.values.getOrElse(shortName, sys.error(s"$shortName of the stack is not defined")))
          stack ← Try(s())
          exists ← Try {
            existsStack(client, stack.stackName.value, log)
          }
          _ ←  {
            if (exists)
              update(client, settings, stack, log)
            else if (stack.ifExistsUpdate)
              create(client, settings, stack, log)
            else
              update(client, settings, stack, log)
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
