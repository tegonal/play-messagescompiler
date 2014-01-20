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
import com.tegonal.resourceparser.parser._

class ResourceBundleTreeSpec extends Specification {
  val emptyResourceBundle = ResourceBundle(Nil)

  val singlePropertyResourceBundle = ResourceBundle(
    Property(
      Path(PathElement("items") :: PathElement("list") :: PathElement("title") :: Nil), PropertyValue("")) :: Nil)

  val multiplePropertiesResourceBundle = ResourceBundle(
    Property(Path(PathElement("items") :: PathElement("list") :: PathElement("title") :: Nil), PropertyValue("Items")) ::
      Property(Path(PathElement("items") :: PathElement("list") :: PathElement("details") :: Nil), PropertyValue("Items detailed")) ::
      Property(Path(PathElement("orders") :: PathElement("title") :: Nil), PropertyValue("Order")) ::
      Nil)

  val nestedPropertiesResourceBundle = ResourceBundle(
    Property(Path(PathElement("items") :: PathElement("list") :: PathElement("title") :: Nil), PropertyValue("Items")) ::
      Property(Path(PathElement("items") :: PathElement("list") :: PathElement("title") :: PathElement("additional") :: Nil), PropertyValue("Items detailed")) ::
      Property(Path(PathElement("orders") :: PathElement("title") :: Nil), PropertyValue("Order")) ::
      Nil)

  val nestedPropertyArgsResourceBundle = ResourceBundle(
    Property(
      Path(PathElement("home") :: PathElement("title") :: Nil), PropertyValue("The list contains {0} items", PropertyValueArg(0) :: Nil)) :: Nil)

  "A tree" should {
    "be created for an empty property" in {
      ResourceBundleTree.create(emptyResourceBundle) === ResourceNode(Nil, Nil)
    }

    "be created for a simple property" in {
      ResourceBundleTree.create(singlePropertyResourceBundle) === ResourceNode(Nil,
        ResourceNode("items" :: Nil,
          ResourceNode("items" :: "list" :: Nil,
            ResourceNode("items" :: "list" :: "title" :: Nil, Nil, true) :: Nil) :: Nil) :: Nil)
    }

    "be created for multiple properties" in {
      ResourceBundleTree.create(multiplePropertiesResourceBundle) === ResourceNode(Nil,
        ResourceNode("items" :: Nil,
          ResourceNode("items" :: "list" :: Nil,
            ResourceNode("items" :: "list" :: "title" :: Nil, Nil, true) :: ResourceNode("items" :: "list" :: "details" :: Nil, Nil, true) :: Nil) :: Nil) ::
          ResourceNode("orders" :: Nil,
            ResourceNode("orders" :: "title" :: Nil, Nil, true) :: Nil, false) :: Nil)
    }

    "be created for nested properties" in {
      ResourceBundleTree.create(nestedPropertiesResourceBundle) === ResourceNode(Nil,
        ResourceNode("items" :: Nil,
          ResourceNode("items" :: "list" :: Nil,
            ResourceNode("items" :: "list" :: "title" :: Nil, ResourceNode("items" :: "list" :: "title" :: "additional" :: Nil, Nil, true) :: Nil, true) :: Nil) :: Nil) ::
          ResourceNode("orders" :: Nil,
            ResourceNode("orders" :: "title" :: Nil, Nil, true) :: Nil) :: Nil)
    }

    "be created for nested properties with args" in {
      ResourceBundleTree.create(nestedPropertyArgsResourceBundle) === ResourceNode(Nil,
        ResourceNode("home" :: Nil,
          ResourceNode("home" :: "title" :: Nil, Nil, true, Arg(0) :: Nil) :: Nil, false, Arg(0) :: Nil) :: Nil)
    }
  }
}