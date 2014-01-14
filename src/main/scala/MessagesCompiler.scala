package com.tegonal.play.plugin.messagescompiler

import sbt.IO
import sbt._
import java.io.File
import java.nio.charset.Charset
import com.tegonal.resourceparser.generator._
import play.PlayExceptions._

object MessagesCompiler {
  def compile(src: File, options: Seq[String]): (String, Option[String], Seq[File]) = {
    try {
      val messages = scala.io.Source.fromFile(src).mkString

      val result = s"""// @SOURCE:${src.getAbsolutePath()}
                      |// @DATE:${new java.util.Date}
                      |${ResourceToScalaGenerator.generateSource(messages, "conf", "messages").get}""".stripMargin

      (result, None, Seq(src))
    } catch {
      case e: Exception =>
        e.printStackTrace
        throw new AssetCompilationException(Some(src), "Internal messages compiler error (see logs)", None, None)
    }
  }

}