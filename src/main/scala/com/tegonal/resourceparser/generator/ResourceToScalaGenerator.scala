/*   __                          __                                          *\
*   / /____ ___ ____  ___  ___ _/ /       resourceparser                      *
*  / __/ -_) _ `/ _ \/ _ \/ _ `/ /        contributed by tegonal              *
*  \__/\__/\_, /\___/_//_/\_,_/_/         http://tegonal.com/                 *
*         /___/                                                               *
*                                                                             *
* This program is free software: you can redistribute it and/or modify it     *
* under the terms of the GNU Lesser General Public License as published by    *
* the Free Software Foundation, either version 3 of the License,              *
* or (at your option) any later version.                                      *
*                                                                             *
* This program is distributed in the hope that it will be useful, but         *
* WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY  *
* or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for *
* more details.                                                               *
*                                                                             *
* You should have received a copy of the GNU General Public License along     *
* with this program. If not, see http://www.gnu.org/licenses/                 *
*                                                                             *
\*                                                                           */
package com.tegonal.resourceparser.generator

import com.tegonal.resourceparser.parser._

object ResourceToScalaGenerator {

  /**
   * Generate Scala source code from a properties file to enable compile safe keys.
   *
   * @param input the input string of a given resource property file
   * @param packageName desired package name of the generated Scala source file, defaults to `com.tegonal.resourceparser`
   * @param objectName desired object name of the generated Scala `object` holding the implicits.
   * @return generated Scala code.
   */
  def generateSource(input: String, packageName: String = "com.tegonal.resourceparser", objectName: String = "ResourceBundleImplicits"): Option[String] = {
    ResourceParser.parse(input) map { parsed =>
      s"""${open(packageName, objectName)}
         |${generate(ResourceBundleTree.create(parsed))}
         |${close}""".stripMargin
    }
  }

  def generate(resourceNode: ResourceNode): String = resourceNode match {
    case ResourceNode(Nil, children, false, _) => children.map(generate).mkString("\n")

    case ResourceNode(path, children, isProperty, args) => createNodeCode(path, children, isProperty, args)
  }

  def createNodeCode(path: Seq[String], children: List[ResourceNode], isProperty: Boolean, args: List[Arg]) = {
    s"""protected case object __${path.map { _.capitalize }.mkString} extends PathElement("${path.last}")${if (isProperty) " with ResourcePath" else ""} {
       |  ${if (isProperty) "def pathElements = " + path.zipWithIndex.map { case (p, i) => "__" + (0 to i).toList.map(path(_).capitalize).mkString }.mkString(" :: ") + " :: Nil" else ""}
       |  ${children.map { c => "def " + escapeReservedWord(c.path.last) + argumentList(args) + " = __" + c.path.map { _.capitalize }.mkString + parameterList(args) }.mkString("\n\n  ")}
       |  ${if (!args.isEmpty && isProperty) "def apply" + argumentList(args) + " = resourceString" + parameterList(args) else ""}
       |}
       |${
      path match {
        case p :: Nil => "\ndef " + escapeReservedWord(p) + { if (isProperty) argumentList(args) else "" } + " = __" + p.capitalize + { if (isProperty) parameterList(args) else "" }
        case _ => ""
      }
    }
       |
       |${children.map(generate).mkString}""".stripMargin
  }

  private def argumentList(args: List[Arg], param: String = ": Any") =
    if (args.isEmpty) "" else s"(${args map (a => s"arg${a.index}$param") mkString (", ")})"

  private def parameterList(args: List[Arg]) =
    argumentList(args, "")

  def open(packageName: String, objectName: String) = s"""package $packageName
                |
                |import play.api.i18n._
                |import scala.language.implicitConversions
                |
                |object $objectName {
                |
                |/**
                | * Definitions
                | */
                |abstract class PathElement(val identifier: String)
                |
                |trait ResourcePath {
                |  def pathElements: Seq[PathElement]
                |
                |  def resourceString(args: Any*) = Messages(pathElements.map(_.identifier).mkString("."), args: _*)
                |}
                |
                |/**
                | * implicit conversion from resource path to Messages
                | */
                |implicit def resourcePath2Messages(resourcePath: ResourcePath): String =
                |  resourcePath.resourceString()
                |
                |""".stripMargin

  val close = "}"

  private def escapeReservedWord(word: String) =
    if (reservedWords.contains(word)) s"`$word`" else word

  private val reservedWords = Set(
    "abstract",
    "case",
    "catch",
    "class",
    "def",
    "do",
    "else",
    "extends",
    "false",
    "final",
    "finally",
    "for",
    "forSome",
    "if",
    "implicit",
    "import",
    "lazy",
    "match",
    "new",
    "null",
    "object",
    "override",
    "package",
    "private",
    "protected",
    "requires",
    "return",
    "sealed",
    "super",
    "this",
    "throw",
    "trait",
    "try",
    "true",
    "type",
    "val",
    "var",
    "while",
    "with",
    "yield",
    "_",
    ":",
    "=",
    "=>",
    "<-",
    "<:",
    "<%",
    ">:",
    "#",
    "@",
    "⇒",
    "←")
}