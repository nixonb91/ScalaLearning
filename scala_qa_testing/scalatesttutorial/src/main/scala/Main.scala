import scala.util.{Try, Success, Failure}
import com.github.t3hnar.bcrypt._
import scalikejdbc._
import ch.qos.logback.classic.{Level, Logger}
import org.slf4j.LoggerFactory

object Main {
  LoggerFactory.getLogger("scalikejdbc").asInstanceOf[Logger].setLevel(Level.WARN)

  val log_suc: String = "[SUCCESS]"
  val log_warn: String = "[WARNING]"
  val log_fail: String = "[FAILURE]"
  val log_info: String = "[INFO]"

  def log(message: String, level: String = s"$log_info") = println(s"$level: $message")

  def cube(x: Int) = {
    x * x * x
  }

  //Create a method to add two numbers and multiply by a third
  def addThenMultiply(x: Int, y: Int)(multiplier: Int): Int = (x + y) * multiplier

  //A method with no parameters
  // def name: String = System.getProperty("user.name")

  //Multi-line method
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
    log("System Starting")

    var x: Int = 2 + 3

    println("Result of addThenMultiply is:\t" + addThenMultiply(1, 2)(3)) 
    // println(s"Hello, $name!")
    println(getSquareString(2.5))

    //notice the method call has no parentheses
    printPitt

    //Make instance of Greeter class with new
    // val greeter = new Greeter("Hello, ", "!")
    // greeter.greet("Scala developer")

    //instantiate case class wtihout new keyword
    // val point = Point(1, 2)
    // val anotherPoint = Point(1, 2)
    // val yetAnotherPoint = Point(2, 2)

    // //Instances of case classes are compared by value, not references
    // if (point == anotherPoint) {
    //   println(s"$point and $anotherPoint are the same")
    // } else {
    //   println(s"$point and $anotherPoint are different")
    // }

    // if (point == yetAnotherPoint) {
    //   println(s"$point and $yetAnotherPoint are the same")
    // } else {
    //   println(s"$point and $yetAnotherPoint are different.")
    // }

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
    println(s"newId is: $newId")
    val newerId: Int = IdFactory.create()
    println(s"newerId is: $newerId")

    //Use the Greeter Traits
    val greeter = new DefaultGreeter()
    greeter.greet("Scala developer")

    val customGreeter = new CustomGreeter("How are you, ", "?")
    customGreeter.greet("Scala developer")
    val n: Int = incr(4)

    println(s"Increment of 4 is: $n")

    var arr: Array[Int] = new Array[Int](2)

    arr(0) = 1
    arr(1) = 2

    log("Array populated", s"$log_suc")

    println(arr(1))

    //Point is now not a case class
    val point1 = new Point(2, 3)
    println(s"The x-coordinate is " + point1.x) //2
    println(s"Point1 is $point1") //(2, 3)

    val defaultPoint = new Point
    println(s"defaultPoint is $defaultPoint")

    //(1, 0)
    val xPoint = new Point(1)
    println(s"xPoint is $xPoint")

    //only set y value by naming the input parameter
    val yPoint = new Point(y = 4)
    println(s"yPoint is $yPoint")

    //Using the getters and setters from Private Point
    val point3 = new PrivatePoint
    point3.x = 15
    point3.y = 101  //should print a warning message

    var intIter = new IntIterator(5)

    while (intIter.hasNext) {
      var temp: Int = intIter.next
      println(s"The next value of intIter is $temp")
    }
    log("Int Iterator Iterated", log_suc)

    println("Going to attempt bcrypt passwords")

    val pw = "password"
    val shouldFail = "farnan"

    // val bc = new UsableBcrypt
    val hashedPW = UsableBcrypt.hashAndSalt(pw)
    val hashedF = UsableBcrypt.hashAndSalt(shouldFail)

    val pwLen = hashedPW.length()
    println(s"The length of hashed password is $pwLen")

    if (UsableBcrypt.verifyHash(pw, hashedPW)) {
      log("Password Verification", log_suc)
    } else {
      log("Password Verification", log_fail)
    }

    if (UsableBcrypt.verifyHash(shouldFail, hashedPW)) {
      log("Farnan Failure", log_suc)
    } else {
      log("Farnan Failure", log_fail)
    }

    if (UsableBcrypt.verifyHash(shouldFail, hashedF)) {
      log("Farnan Pass", log_suc)
    } else {
      log("Farnan Pass", log_fail)
    }

    log("Attempting to establish scalikejdbc operations", log_info)

    log("Initiating JDBC Driver and Connection Pool", log_info)

    Class.forName("org.h2.Driver")
    ConnectionPool.singleton("jdbc:h2:mem:hello", "user", "pass")

    log("Creating ad-hoc session provider on the REPL", log_info)

    implicit val session = AutoSession

    log("Creating SQL Table", log_info)

    sql"""
    create table members (
      id serial not null primary key,
      name varchar(64),
      created_at timestamp not null
    )
    """.execute.apply()

    log("Table Created", log_suc)

    log("Inserting Initial Data", log_info)

    Seq("Alice", "Bob", "Charlie") foreach {name => sql"insert into members (name, created_at) values (${name}, current_timestamp)".update.apply()}

    log("Inserted Initial Values", log_suc)

    //for now retrieves all data as Map value
    val entities: List[Map[String, Any]] = sql"select * from members".map(_.toMap).list.apply()

    log("Selected Values into List", log_suc)

    //defines Entity Object and Extractor
    import java.time._
    case class Member(id: Long, name: Option[String], createdAt: ZonedDateTime)
    object Member extends SQLSyntaxSupport[Member] {
      override val tableName = "members"
      def apply(rs: WrappedResultSet) = new Member(rs.long("id"), rs.stringOpt("name"), rs.zonedDateTime("created_at"))
    }

    //find all members
    val members: List[Member] = sql"select * from members".map(rs => Member(rs)).list.apply()

    //Use Paste Mode on Scala REPL
    val m = Member.syntax("m")
    val name = "Alice"
    val alice: Option[Member] = withSQL {
      select.from(Member as m).where.eq(m.name, name)
    }.map(rs => Member(rs)).single.apply()
  }
}