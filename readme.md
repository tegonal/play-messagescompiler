# play-messagescompiler - Messages compiler for Play 2 Scala

## About

This humble Play plugin provides type safety for the project's messages. It adds key literals instead of using strings to reference a property.


    Messages("home.title")
    // becomes
    Messages(home.title)

Using the generated key literals saves the trouble of misspelt keys that lead to non-translated properties. A nonexistent or changed key will lead to a compilation error.

## Installation

Add the following to `project/plugins.sbt`:

    resolvers += "Tegonal releases" at "https://github.com/tegonal/tegonal-mvn/raw/master/releases/"

    addSbtPlugin("com.tegonal" % "play-messagescompiler" % "1.0.0")

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
        Ok(views.html.index(Messages(home.title)))
      }
    
    }

## License

GNU Lesser General Public License http://www.gnu.org/licenses/lgpl.html
