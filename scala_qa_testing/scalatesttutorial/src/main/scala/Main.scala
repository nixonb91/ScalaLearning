object Main {
  def cube(x: Int) = {
    x * x * x
  }

  //Create a method to add two numbers and multiply by a third
  def addThenMultiply(x: Int, y: Int)(multiplier: Int): Int = (x + y) * multiplier
  def name: String = System.getProperty("user.name")

  def getSquareString(input: Double): String = {
    val square = input * input

    //last expression is the return value
    square.toString
  }

  //Create a funcion to increment x
  val incr = (x: Int) => x + 1
  

  //Create a method with no return value, Unit (void in java or C)
  def printPitt: Unit = println("Hail to Pitt!")

  //void main(String[] args)
  def main(args: Array[String]): Unit = {
    var x: Int = 2 + 3

    

    println("Result of addThenMultiply is:\t" + addThenMultiply(1, 2)(3)) 

    //A method with no parameters

    //notice the method call has no parentheses
    println("Hello, " + name + "!")

    //Multi-line method
    
    println(getSquareString(2.5))
    printPitt

    //Make instance of Greeter class with new
    // val greeter = new Greeter("Hello, ", "!")
    // greeter.greet("Scala developer")

    //instantiate case class wtihout new keyword
    val point = Point(1, 2)
    val anotherPoint = Point(1, 2)
    val yetAnotherPoint = Point(2, 2)

    //Instances of case classes are compared by value, not references
    if (point == anotherPoint) {
      println(point + " and " + anotherPoint + " are the same.")
    } else {
      println(point + " and " + anotherPoint + " are different.")
    }

    if (point == yetAnotherPoint) {
      println(point + " and " + yetAnotherPoint + " are the same.")
    } else {
      println(point + " and " + yetAnotherPoint + " are different.")
    }

    //Define an Object - a single instance of its definition
    object IdFactory {
      private var counter = 0 //private var in object definition
      def create(): Int = {
        counter += 1
        counter //counter is returned
      }
    }

    //Access the object by referring to its name
    val newId: Int = IdFactory.create()
    println("newId is:\t" + newId)
    val newerId: Int = IdFactory.create()
    println("newerId is:\t" + newerId)

    //Use the Greeter Traits
    val greeter = new DefaultGreeter()
    greeter.greet("Scala developer")

    val customGreeter = new CustomGreeter("How are you,", "?")
    customGreeter.greet("Scala developer")

    println("Increment of 4 is:\t" + incr(4))
  }
}