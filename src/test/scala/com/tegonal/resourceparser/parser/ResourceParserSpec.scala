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
package com.tegonal.resourceparser.parser

import org.specs2.mutable._

class ResourceParserSpec extends Specification {

  val resourceParser = new ResourceParser

  "Resource file parser" should {
    "succeed with simple properties" in {
      resourceParser.parse("items=Items").successful === true
    }

    "succeed with detail properties" in {
      resourceParser.parse("items.details.title=This is the detail title").successful === true
    }

    "fail without equals sign" in {
      resourceParser.parse("items.details.titleThis is the detail title").successful === false
    }

    "succeed with two equals as property values allow any character" in {
      resourceParser.parse("items.details.title==This is the detail title").successful === true
    }

    "result in resource file ast" in {
      resourceParser.parse("items=Items").get === ResourceBundle(Property(Path(PathElement("items") :: Nil), PropertyValue("Items")) :: Nil)
    }

    "succeed with multi lines" in {
      resourceParser.parse("""items=Items
                             |items.details.title=This is the detail title
                             |
                             |users=Users
                             |users.details.title=User Details""".stripMargin).get ===
        ResourceBundle(
          Property(Path(PathElement("items") :: Nil), PropertyValue("Items")) ::
            Property(Path(PathElement("items") :: PathElement("details") :: PathElement("title") :: Nil), PropertyValue("This is the detail title")) ::
            Property(Path(PathElement("users") :: Nil), PropertyValue("Users")) ::
            Property(Path(PathElement("users") :: PathElement("details") :: PathElement("title") :: Nil), PropertyValue("User Details")) ::
            Nil)

    }

    "succeed with comments in file" in {
      resourceParser.parse("#Auftrag\nthing=Title").get === ResourceBundle(Comment("Auftrag") :: Property(Path(PathElement("thing") :: Nil), PropertyValue("Title")) :: Nil)
    }

  }

}
