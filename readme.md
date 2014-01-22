# play-messagescompiler - Messages compiler for Play 2 Scala

## About

This humble Play plugin provides type safety for the project's messages. It adds key literals instead of using strings to reference a property.


    Messages("home.title")
    // becomes
    home.title

Using the generated key literals saves the trouble of misspelt keys that lead to non-translated properties. A nonexistent or changed key will lead to a compilation error.

## Installation

Add the following to `project/plugins.sbt`:

    resolvers += "Tegonal releases" at "https://github.com/tegonal/tegonal-mvn/raw/master/releases/"

    addSbtPlugin("com.tegonal" % "play-messagescompiler" % "1.0.2")

## Usage

The file `conf/messages` will be compiled to `target/scala*/src_managed/main/conf/messages.scala`

    home.title=Space: the final frontier

After importing conf.messages._ the key literals are available within your code.

    package controllers
    
    import play.api._
    import play.api.mvc._
    import play.api.i18n._
    import conf.messages._
    
    object Application extends Controller {
    
      def index = Action {
        Ok(views.html.index(home.title))
      }
    
    }

The plugin also supports message formats:

    // conf/messages
    home.title=Space: the final frontier, Stardate: {0}

    // within a controller
    Ok(views.html.index(home.title("44390.1")))
    
    // where this wouldn't compile
    Ok(views.html.index(home.title))

## Ideas

- Type checked `java.text.MessageFormat` arguments (number, date, time, choice).

## License

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
