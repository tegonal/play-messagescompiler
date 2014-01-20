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
import scala.collection.mutable.ListBuffer

class ResourceBundleTree {

  val resources: scala.collection.mutable.Map[Path, ResourceNodeBuffer] = new scala.collection.mutable.HashMap

  def create(resourceComponent: ResourceComponent): ResourceNode = {
    resourceComponent match {
      case ResourceBundle(elements) => {
        resources.put(Path(Nil), ResourceNodeBuffer(Nil, ListBuffer.empty, false))

        // ignoring comments so far
        elements collect { case e: Property => e } map (createProperty(_))
      }
    }

    resources(Path(Nil)).toResourceNode
  }

  // TODO simplify
  def createProperty(property: Property) = property match {
    case Property(path, PropertyValue(_, propertyValueArgs)) =>
      val args = propertyValueArgs map (a => Arg(a.index))

      // ResourceNode already exists
      resources.get(path) map { existing =>
        // just update the path
        existing.isProperty = true
        existing.args = args
      } getOrElse {
        // create the ResourceNode
        val created = ResourceNodeBuffer(path.pathElements map (_.name), ListBuffer.empty, true, args)
        resources.put(path, created)
        // add child to parent
        resources.get(Path(path.pathElements.init)) map { parent =>
          parent.children += created
        } getOrElse {
          createHierarchy(Path(path.pathElements.init), created, args)
        }
      }
  }

  def createHierarchy(path: Path, child: ResourceNodeBuffer, args: List[Arg]): Unit = path match {
    case Path(pathElements) => {
      resources.get(path) map { existing =>
        existing.children += child
      } getOrElse {
        val created = ResourceNodeBuffer(pathElements map (_.name), ListBuffer.empty += child, false, args)
        resources.put(path, created)
        createHierarchy(Path(pathElements.init), created, args)
      }
    }
  }
}

object ResourceBundleTree {
  def create(resourceComponent: ResourceComponent): ResourceNode = (new ResourceBundleTree).create(resourceComponent)
}

case class ResourceNodeBuffer(val path: Seq[String], val children: scala.collection.mutable.ListBuffer[ResourceNodeBuffer], var isProperty: Boolean, var args: List[Arg] = Nil) {
  def toResourceNode: ResourceNode = ResourceNode(path, children.toList.map(_.toResourceNode), isProperty, args)
}