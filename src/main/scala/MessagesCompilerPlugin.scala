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
package com.tegonal.play.plugin.messagescompiler

import sbt._
import sbt.Keys._
import play.Project._
import play.PlayExceptions.AssetCompilationException

object MessagesCompilerPlugin extends Plugin {
  val id = "play-messagescompiler"
  val entryPoints = SettingKey[PathFinder](id + "-entry-points")
  val options = SettingKey[Seq[String]](id + "-options")

  val messagesWatcher = MessagesFilesWatcher(
    id,
    (_ ** "messages"),
    entryPoints in Compile,
    { MessagesCompiler.compile _ },
    options in Compile)

  override val settings = Seq(
    entryPoints <<= (confDirectory in Compile)(base => (base ** "messages")),
    options := Seq.empty[String],
    sourceGenerators in Compile <+= messagesWatcher)

  def MessagesFilesWatcher(
    name: String,
    watch: File => PathFinder,
    filesSetting: sbt.SettingKey[PathFinder],
    compile: (File, Seq[String]) => (String, Option[String], Seq[File]),
    optionsSettings: sbt.SettingKey[Seq[String]]) =
    (state, confDirectory in Compile, sourceManaged in Compile, cacheDirectory, optionsSettings, filesSetting) map { (state, conf, sourceManaged, cache, options, files) =>

      import java.io._

      val cacheFile = cache / name
      val currentInfos = watch(conf).get.map(f => f -> FileInfo.lastModified(f)).toMap

      val (previousRelation, previousInfo) = Sync.readInfo(cacheFile)(FileInfo.lastModified.format)

      if (previousInfo != currentInfos) {
        lazy val changedFiles: Seq[File] = currentInfos.filter(e => !previousInfo.get(e._1).isDefined || previousInfo(e._1).lastModified < e._2.lastModified).map(_._1).toSeq ++ previousInfo.filter(e => !currentInfos.get(e._1).isDefined).map(_._1).toSeq

        val dependencies = previousRelation.filter((original, compiled) => changedFiles.contains(original))._2s
        dependencies.foreach(IO.delete)

        val generated: Seq[(File, java.io.File)] = (files pair relativeTo(Seq(conf))).flatMap {
          case (sourceFile, name) => {
            if (changedFiles.contains(sourceFile)) {
              val (debug, min, dependencies) = try {
                compile(sourceFile, options)
              } catch {
                case e: AssetCompilationException => throw play.PlaySourceGenerators.reportCompilationError(state, e)
              }

              val out = new File(sourceManaged, "conf/" + name + ".scala")
              IO.write(out, debug)

              (dependencies ++ Seq(sourceFile)).toSet[File].map(_ -> out)
            } else {
              previousRelation.filter((original, compiled) => original == sourceFile)._2s.map(sourceFile -> _)
            }
          }
        }

        // write object graph to cache file 
        Sync.writeInfo(cacheFile,
          Relation.empty[File, File] ++ generated,
          currentInfos)(FileInfo.lastModified.format)

        // Return new files
        generated.map(_._2).distinct.toList

      } else {
        // Return previously generated files
        previousRelation._2s.toSeq
      }
    }

}
