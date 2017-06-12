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

import org.specs2.mutable._
import com.tegonal.resourceparser.parser.ResourceParser

class ResourceToScalaGeneratorSpec extends Specification {
  val resourceFile = """items.details=Item details
                       |items.list.title=Items
                       |orders.list.title=Orders
                       |
                       |
                       |orders.details.title=Order
                       |""".stripMargin

  val keywordsResourceFile = """type=Type"""

  val resourceFileWithArgs = """home.title.noargs=No arg message
                               |home.title.onearg={0} Arg
                               |home.title.twoargs={0} and {1} Args""".stripMargin

  val expected = """package com.tegonal.resourceparser
                   |
                   |import play.api.i18n._
                   |import scala.language.implicitConversions
                   |
                   |object ResourceBundleImplicits {
                   |
                   |/**
                   | * Definitions
                   | */
                   |abstract class PathElement(val identifier: String)
                   |
                   |trait ResourcePath {
                   |  def pathElements: Seq[PathElement]
                   |
                   |  def resourceString(args: Any*)(implicit lang: Lang) = Messages(pathElements.map(_.identifier).mkString("."), args: _*)
                   |
                   |  def msg(args: Any*)(implicit lang: Lang) = resourceString(args)
                   |}
                   |
                   |/**
                   | * implicit conversion from resource path to Messages
                   | */
                   |implicit def resourcePath2Messages(resourcePath: ResourcePath)(implicit lang: Lang): String =
                   |  resourcePath.resourceString()
                   |
                   |protected case object __Items extends PathElement("items") {
                   |
                   |  def details = __ItemsDetails
                   |
                   |  def list = __ItemsList
                   |}
                   |
                   |def items = __Items
                   |
                   |protected case object __ItemsDetails extends PathElement("details") with ResourcePath {
                   |  def pathElements = __Items :: __ItemsDetails :: Nil
                   |
                   |}
                   |
                   |protected case object __ItemsList extends PathElement("list") {
                   |
                   |  def title = __ItemsListTitle
                   |}
                   |
                   |protected case object __ItemsListTitle extends PathElement("title") with ResourcePath {
                   |  def pathElements = __Items :: __ItemsList :: __ItemsListTitle :: Nil
                   |
                   |}
                   |
                   |protected case object __Orders extends PathElement("orders") {
                   |
                   |  def list = __OrdersList
                   |
                   |  def details = __OrdersDetails
                   |}
                   |
                   |def orders = __Orders
                   |
                   |protected case object __OrdersList extends PathElement("list") {
                   |
                   |  def title = __OrdersListTitle
                   |}
                   |
                   |protected case object __OrdersListTitle extends PathElement("title") with ResourcePath {
                   |  def pathElements = __Orders :: __OrdersList :: __OrdersListTitle :: Nil
                   |
                   |}
                   |
                   |protected case object __OrdersDetails extends PathElement("details") {
                   |
                   |  def title = __OrdersDetailsTitle
                   |}
                   |
                   |protected case object __OrdersDetailsTitle extends PathElement("title") with ResourcePath {
                   |  def pathElements = __Orders :: __OrdersDetails :: __OrdersDetailsTitle :: Nil
                   |
                   |}
                   |}""".stripMargin

  val keywordsExpected = """package com.tegonal.resourceparser
                           |
                           |import play.api.i18n._
                           |import scala.language.implicitConversions
                           |
                           |object ResourceBundleImplicits {
                           |
                           |/**
                           | * Definitions
                           | */
                           |abstract class PathElement(val identifier: String)
                           |
                           |trait ResourcePath {
                           |  def pathElements: Seq[PathElement]
                           |
                           |  def resourceString(args: Any*)(implicit lang: Lang) = Messages(pathElements.map(_.identifier).mkString("."), args: _*)
                           |
                           |  def msg(args: Any*)(implicit lang: Lang) = resourceString(args)
                           |}
                           |
                           |/**
                           | * implicit conversion from resource path to Messages
                           | */
                           |implicit def resourcePath2Messages(resourcePath: ResourcePath)(implicit lang: Lang): String =
                           |  resourcePath.resourceString()
                           |
                           |protected case object __Type extends PathElement("type") with ResourcePath {
                           |  def pathElements = __Type :: Nil
                           |}
                           |
                           |def `type` = __Type
                           |
                           |}""".stripMargin

  val argsExpected = """package com.tegonal.resourceparser
                       |
                       |import play.api.i18n._
                       |import scala.language.implicitConversions
                       |
                       |object ResourceBundleImplicits {
                       |
                       |/**
                       | * Definitions
                       | */
                       |abstract class PathElement(val identifier: String)
                       |
                       |trait ResourcePath {
                       |  def pathElements: Seq[PathElement]
                       |
                       |  def resourceString(args: Any*)(implicit lang: Lang) = Messages(pathElements.map(_.identifier).mkString("."), args: _*)
                       |
                       |  def msg(args: Any*)(implicit lang: Lang) = resourceString(args)
                       |}
                       |
                       |/**
                       | * implicit conversion from resource path to Messages
                       | */
                       |implicit def resourcePath2Messages(resourcePath: ResourcePath)(implicit lang: Lang): String =
                       |  resourcePath.resourceString()
                       |
                       |
                       |protected case object __Home extends PathElement("home") {
                       |
                       |  def title = __HomeTitle
                       |
                       |}
                       |
                       |def home = __Home
                       |
                       |protected case object __HomeTitle extends PathElement("title") {
                       |
                       |  def noargs = __HomeTitleNoargs
                       |
                       |  def onearg(arg0: Any) = __HomeTitleOnearg(arg0)
                       |
                       |  def twoargs(arg0: Any, arg1: Any) = __HomeTitleTwoargs(arg0, arg1)
                       |
                       |}
                       |
                       |
                       |protected case object __HomeTitleNoargs extends PathElement("noargs") with ResourcePath {
                       |  def pathElements = __Home :: __HomeTitle :: __HomeTitleNoargs :: Nil
                       |
                       |
                       |}
                       |
                       |
                       |protected case object __HomeTitleOnearg extends PathElement("onearg") with ResourcePath {
                       |  def pathElements = __Home :: __HomeTitle :: __HomeTitleOnearg :: Nil
                       |
                       |  def apply(arg0: Any) = resourceString(arg0)
                       |}
                       |
                       |
                       |protected case object __HomeTitleTwoargs extends PathElement("twoargs") with ResourcePath {
                       |  def pathElements = __Home :: __HomeTitle :: __HomeTitleTwoargs :: Nil
                       |
                       |  def apply(arg0: Any, arg1: Any) = resourceString(arg0, arg1)
                       |}
                       |
                       }""".stripMargin

  "The generator" should {
    "generate Scala source code" in {
      val result = ResourceToScalaGenerator.generateSource(resourceFile).get
      result.replaceAll("""[\n|\s]""", "") === expected.replaceAll("""[\n|\s]""", "")
    }

    "generate Scala keyword safe code" in {
      val result = ResourceToScalaGenerator.generateSource(keywordsResourceFile).get
      result.replaceAll("""[\n|\s]""", "") === keywordsExpected.replaceAll("""[\n|\s]""", "")
    }

    "generate Scala code with arguments" in {
      val result = ResourceToScalaGenerator.generateSource(resourceFileWithArgs).get
      result.replaceAll("""[\n|\s]""", "") === argsExpected.replaceAll("""[\n|\s]""", "")
    }

    "generate Scala code with arguments" in {
      val result = ResourceToScalaGenerator.generateSource(resourceFileWithArgs).get
      result.replaceAll("""[\n|\s]""", "") === argsExpected.replaceAll("""[\n|\s]""", "")
    }
  }
}
