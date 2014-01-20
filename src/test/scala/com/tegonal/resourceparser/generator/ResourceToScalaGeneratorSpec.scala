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

  val expected = """package com.tegonal.resourceparser
                   |
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
                   |  def resourceString = pathElements.map(_.identifier).mkString(".")
                   |}
                   |
                   |/**
                   | * implicit conversion from resource path to string
                   | */
                   |implicit def resourcePath2String(resourcePath: ResourcePath): String =
                   |  resourcePath.resourceString
                   |
                   |case object Items extends PathElement("items") {
                   |
                   |  def details = ItemsDetails
                   |
                   |  def list = ItemsList
                   |}
                   |
                   |def items = Items
                   |
                   |case object ItemsDetails extends PathElement("details") with ResourcePath {
                   |  def pathElements = Items :: ItemsDetails :: Nil
                   |
                   |}
                   |
                   |case object ItemsList extends PathElement("list") {
                   |
                   |  def title = ItemsListTitle
                   |}
                   |
                   |case object ItemsListTitle extends PathElement("title") with ResourcePath {
                   |  def pathElements = Items :: ItemsList :: ItemsListTitle :: Nil
                   |
                   |}
                   |
                   |case object Orders extends PathElement("orders") {
                   |
                   |  def list = OrdersList
                   |
                   |  def details = OrdersDetails
                   |}
                   |
                   |def orders = Orders
                   |
                   |case object OrdersList extends PathElement("list") {
                   |
                   |  def title = OrdersListTitle
                   |}
                   |
                   |case object OrdersListTitle extends PathElement("title") with ResourcePath {
                   |  def pathElements = Orders :: OrdersList :: OrdersListTitle :: Nil
                   |
                   |}
                   |
                   |case object OrdersDetails extends PathElement("details") {
                   |
                   |  def title = OrdersDetailsTitle
                   |}
                   |
                   |case object OrdersDetailsTitle extends PathElement("title") with ResourcePath {
                   |  def pathElements = Orders :: OrdersDetails :: OrdersDetailsTitle :: Nil
                   |
                   |}
                   |}""".stripMargin

  val keywordsExpected = """package com.tegonal.resourceparser
                           |
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
                           |  def resourceString = pathElements.map(_.identifier).mkString(".")
                           |}
                           |
                           |/**
                           | * implicit conversion from resource path to string
                           | */
                           |implicit def resourcePath2String(resourcePath: ResourcePath): String =
                           |  resourcePath.resourceString
                           |
                           |case object Type extends PathElement("type") with ResourcePath {
                           |  def pathElements = Type :: Nil
                           |}
                           |
                           |def `type` = Type
                           |
                           |}""".stripMargin

  "The generator" should {
    "generate Scala source code" in {
      val result = ResourceToScalaGenerator.generateSource(resourceFile).get
      result.replaceAll("""[\n|\s]""", "") === expected.replaceAll("""[\n|\s]""", "")
    }

    "generate Scala keyword safe code" in {
      val result = ResourceToScalaGenerator.generateSource(keywordsResourceFile).get
      result.replaceAll("""[\n|\s]""", "") === keywordsExpected.replaceAll("""[\n|\s]""", "")
    }
  }
}

