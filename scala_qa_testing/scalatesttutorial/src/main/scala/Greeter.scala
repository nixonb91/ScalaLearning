/**
* Create a trait, an ADT in Scala used for
* extending multiple traits to a file
*/

trait Greeter {
    def greet(name: String): Unit =
        println("Hello, " + name + "!")     //default implementation
}