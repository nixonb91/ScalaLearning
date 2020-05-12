class DefaultGreeter extends Greeter

class CustomGreeter(prefix: String, postfix: String) extends Greeter {
    override def greet(name: String): Unit = {
        println(prefix + name + postfix)
    }
}