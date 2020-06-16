import scala.util.{Try, Success, Failure, Random}
// import com.github.t3hnar.bcrypt._
// import scalikejdbc._
import ch.qos.logback.classic.{Level, Logger}
import org.slf4j.LoggerFactory
import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global
import scala.language.postfixOps
// import play.api.libs.json._
// import reactivemongo.play.json._

//Import Data for MongoDB
import org.mongodb.scala._
import org.mongodb.scala.model.Aggregates._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Sorts._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model._
import org.mongodb.scala.connection.ClusterSettings
import org.mongodb.scala.connection.ClusterSettings.Builder
import org.mongodb.scala.MongoClientSettings
import org.mongodb.scala.MongoCredential._
// import org.mongodb.scala.connection.netty
// import com.mongodb.MongoClientSettings
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromRegistries, fromProviders, fromCodecs}

import scala.collection.JavaConverters._
import tour.Helpers._

object Main {
  LoggerFactory.getLogger("scalikejdbc").asInstanceOf[Logger].setLevel(Level.WARN)
  LoggerFactory.getLogger("org.mongodb.driver").asInstanceOf[Logger].setLevel(Level.WARN)

  val log_suc: String = "[SUCCESS]"
  val log_warn: String = "[WARNING]"
  val log_fail: String = "[FAILURE]"
  val log_info: String = "[INFO]"

  def log(message: String, level: String = s"$log_info") = println(s"$level: $message")

  // def cube(x: Int) = {
  //   x * x * x
  // }

  // //Create a method to add two numbers and multiply by a third
  // def addThenMultiply(x: Int, y: Int)(multiplier: Int): Int = (x + y) * multiplier

  // //A method with no parameters
  // // def name: String = System.getProperty("user.name")

  // //Multi-line method
  // def getSquareString(input: Double): String = {
  //   val square = input * input

  //   //last expression is the return value
  //   square.toString
  // }

  // //Create a funcion to increment x
  // val incr = (x: Int) => x + 1
  

  // //Create a method with no return value, Unit (void in java or C)
  // def printPitt: Unit = println("Hail to Pitt!")

  //void main(String[] args)
  def main(args: Array[String]): Unit = {
    log("System Starting")

  //   var x: Int = 2 + 3

  //   println("Result of addThenMultiply is:\t" + addThenMultiply(1, 2)(3)) 
  //   // println(s"Hello, $name!")
  //   println(getSquareString(2.5))

  //   //notice the method call has no parentheses
  //   printPitt

  //   //Make instance of Greeter class with new
  //   // val greeter = new Greeter("Hello, ", "!")
  //   // greeter.greet("Scala developer")

  //   //instantiate case class wtihout new keyword
  //   // val point = Point(1, 2)
  //   // val anotherPoint = Point(1, 2)
  //   // val yetAnotherPoint = Point(2, 2)

  //   // //Instances of case classes are compared by value, not references
  //   // if (point == anotherPoint) {
  //   //   println(s"$point and $anotherPoint are the same")
  //   // } else {
  //   //   println(s"$point and $anotherPoint are different")
  //   // }

  //   // if (point == yetAnotherPoint) {
  //   //   println(s"$point and $yetAnotherPoint are the same")
  //   // } else {
  //   //   println(s"$point and $yetAnotherPoint are different.")
  //   // }

  //   //Define an Object - a single instance of its definition
  //   object IdFactory {
  //     private var counter = 0 //private var in object definition
  //     def create(): Int = {
  //       counter += 1
  //       counter //counter is returned
  //     }
  //   }

  //   //Access the object by referring to its name
  //   val newId: Int = IdFactory.create()
  //   println(s"newId is: $newId")
  //   val newerId: Int = IdFactory.create()
  //   println(s"newerId is: $newerId")

  //   //Use the Greeter Traits
  //   val greeter = new DefaultGreeter()
  //   greeter.greet("Scala developer")

  //   val customGreeter = new CustomGreeter("How are you, ", "?")
  //   customGreeter.greet("Scala developer")
  //   val n: Int = incr(4)

  //   println(s"Increment of 4 is: $n")

  //   var arr: Array[Int] = new Array[Int](2)

  //   arr(0) = 1
  //   arr(1) = 2

  //   log("Array populated", s"$log_suc")

  //   println(arr(1))

  //   //Point is now not a case class
  //   val point1 = new Point(2, 3)
  //   println(s"The x-coordinate is " + point1.x) //2
  //   println(s"Point1 is $point1") //(2, 3)

  //   val defaultPoint = new Point
  //   println(s"defaultPoint is $defaultPoint")

  //   //(1, 0)
  //   val xPoint = new Point(1)
  //   println(s"xPoint is $xPoint")

  //   //only set y value by naming the input parameter
  //   val yPoint = new Point(y = 4)
  //   println(s"yPoint is $yPoint")

  //   //Using the getters and setters from Private Point
  //   val point3 = new PrivatePoint
  //   point3.x = 15
  //   point3.y = 101  //should print a warning message

  //   var intIter = new IntIterator(5)

  //   while (intIter.hasNext) {
  //     var temp: Int = intIter.next
  //     println(s"The next value of intIter is $temp")
  //   }
  //   log("Int Iterator Iterated", log_suc)

  //   println("Going to attempt bcrypt passwords")

  //   val pw = "password"
  //   val shouldFail = "farnan"

  //   // val bc = new UsableBcrypt
  //   val hashedPW = UsableBcrypt.hashAndSalt(pw)
  //   val hashedF = UsableBcrypt.hashAndSalt(shouldFail)

  //   val pwLen = hashedPW.length()
  //   println(s"The length of hashed password is $pwLen")

  //   if (UsableBcrypt.verifyHash(pw, hashedPW)) {
  //     log("Password Verification", log_suc)
  //   } else {
  //     log("Password Verification", log_fail)
  //   }

  //   if (UsableBcrypt.verifyHash(shouldFail, hashedPW)) {
  //     log("Farnan Failure", log_suc)
  //   } else {
  //     log("Farnan Failure", log_fail)
  //   }

  //   if (UsableBcrypt.verifyHash(shouldFail, hashedF)) {
  //     log("Farnan Pass", log_suc)
  //   } else {
  //     log("Farnan Pass", log_fail)
  //   }

  //   log("Attempting to establish scalikejdbc operations", log_info)

  //   log("Initiating JDBC Driver and Connection Pool", log_info)

  //   Class.forName("org.h2.Driver")
  //   ConnectionPool.singleton("jdbc:h2:mem:hello", "user", "pass")

  //   log("Creating ad-hoc session provider on the REPL", log_info)

  //   implicit val session = AutoSession

  //   log("Creating SQL Table", log_info)

  //   sql"""
  //   create table members (
  //     id serial not null primary key,
  //     name varchar(64),
  //     created_at timestamp not null
  //   )
  //   """.execute.apply()

  //   log("Table Created", log_suc)

  //   log("Inserting Initial Data", log_info)

  //   Seq("Alice", "Bob", "Charlie") foreach {name => sql"insert into members (name, created_at) values (${name}, current_timestamp)".update.apply()}

  //   log("Inserted Initial Values", log_suc)

  //   //for now retrieves all data as Map value
  //   val entities: List[Map[String, Any]] = sql"select * from members".map(_.toMap).list.apply()

  //   log("Selected Values into List", log_suc)

  //   //defines Entity Object and Extractor
  //   import java.time._
  //   case class Member(id: Long, name: Option[String], createdAt: ZonedDateTime)
  //   object Member extends SQLSyntaxSupport[Member] {
  //     override val tableName = "members"
  //     def apply(rs: WrappedResultSet) = new Member(rs.long("id"), rs.stringOpt("name"), rs.zonedDateTime("created_at"))
  //   }

  //   //find all members
  //   val members: List[Member] = sql"select * from members".map(rs => Member(rs)).list.apply()

  //   //Use Paste Mode on Scala REPL
  //   val m = Member.syntax("m")
  //   val name = "Alice"
  //   val alice: Option[Member] = withSQL {
  //     select.from(Member as m).where.eq(m.name, name)
  //   }.map(rs => Member(rs)).single.apply()

  //Attempt to use MongoDB from Quick Guide

  //Get instance of Mongo Client
  
  // val mongoClient: MongoClient = MongoClient  
  // (
  // MongoClientSettings.builder()
  //   .applyToClusterSettings((builder: ClusterSettings.Builder) => builder.hosts(List(new ServerAddress("hostOne")).asJava))
  //   .build())
  // val address: ServerAddress = ServerAddress("https://localhost", 27017)
  // val hostList: List[ServerAddress] = List(address)

  // val clusterSettings: ClusterSettings = ClusterSettings
  //     .builder()
  //     // .description("A simple test cluster for demoing MongoDB")
  //     .hosts(List(new ServerAddress("localhost:27017")).asJava)
  //     .build()

  // val mongoClientSettings: MongoClientSettings = MongoClientSettings
  //   .builder()
  //   .applyToClusterSettings((b: ClusterSettings.Builder) => b.applySettings(clusterSettings))
  //   .build()

  // val promise = Promise[Boolean]

  val user: String = "anyplace"
  val source: String = "caprioDB" //the source where the user is defined/stored
  val password: String= "nopass"
  val clusterName: String = "capriocluster"

  log("Attemtipng to connect to client", log_info)
  //s"mongodb+srv://$user:$password@$clusterName-ihd8s.gcp.mongodb.net/$source?retryWrites=true&w=majority"
  val uri: String = (s"mongodb+srv://$user:$password@$clusterName-ihd8s.gcp.mongodb.net/$source?retryWrites=true&w=majority")

  log("At the routeresult", log_info)
  val rand: Random = Random
  val x: Double = rand.nextDouble
  val y: Double = rand.nextDouble
  val rr: RouteResult = RouteResult(x, y, "source->testing->sink")
  log("At the Codec", log_info)
  val codecRegistry = fromRegistries(fromProviders(classOf[RouteResult]), DEFAULT_CODEC_REGISTRY)
  
  // //Get Mongo Client
  log("Am I here?", log_info)
  val mongoClient: MongoClient = MongoClient (uri)
  log("Connection Established", log_info)

  // //Get Mongo Database from Client
  val db: MongoDatabase = mongoClient.getDatabase("mydb").withCodecRegistry(codecRegistry)

  //Test with CaseClass
  

  //Get the Collection holding Document Objects
  val collection: MongoCollection[RouteResult] = db.getCollection("test")
  log("MongoDB Collection retrieval", log_suc)

  // val subsciption = new Observer[Completed] {
  //   override def onNext(result: Completed): Unit = println("Inserted")

  //   override def onError(e: Throwable): Unit = {
  //     println("Failed" + e.toString)
  //     promise.success(false)
  //   }

  //   override def onComplete(): Unit = {
  //     println("Completed")
  //     promise.success(true)
  //   }
  // }

  //Create a Document in JSON format to be later inserted, note the ke-values

  

  // //Test using a string JSON object to create a Document
  // val strJSON: String = "{name: \"Brian\", info: \"StringDocTest\"}"
  // // val strParsed = Json.parse(strJSON).as[JsObject]
  // val doc: Document = Document.apply(strJSON)

  // val doc: Document = Document("name" -> "plzwork", "type" -> "database",
  //                             "count" -> 1, "info" -> Document("x" -> 203, "y" -> 102))

  collection.insertOne(rr).results()

  // Await.result(observable.toFuture, Duration.Inf)

  val list = Seq (
    RouteResult(1, 1, "List Item One"),
    RouteResult(2, 2, "List Item Two"),
    RouteResult(3, 3, "List Item Three")
  )

  
  log("MongoDB, Document Inserted to Test Collection", log_suc)

  // //Insert Multiple Documents
  // val documents = (1 to 100) map { i: Int => Document("i" -> i)}

  // //not yet subscribed
  val insertObservable = collection.insertMany(list).results()

  // //Count the documents in test collection
  // val insertAndCount = for {
  //   insertResult <- insertObservable
  //   countResult <- collection.countDocuments()
  // } yield countResult

  // println(s"total # of documents after inserting 100 small ones: ${insertAndCount.headResult()}")
  // collection.find(exists("_id", exists = false)).first().printHeadResult()
  collection.find().printResults()

  // log("Query Initiated", log_suc)

  // //Find all the documents in the collection
  // collection.find().printResults()

  // log("Queried All Documents in Collection", log_suc)

  // //Find a single document in the collection
  // collection.find(equal("i", 71)).first().printHeadResult()

  // log("Queried for i=71", log_suc)

  // //Get all documents in collection
  // collection.find().printResults()

  // //Get a set of documents from collection
  // collection.find(gt("i", 50)).printResults()
  // //or do, collection.find(and(gt("i", 50), lte("i", 100))).printResults()

  // //sort queried documents by descending i's
  // collection.find(exists("i")).sort(descending("i")).first().printHeadResult()

  // log("Query Sorted", log_suc)

  // //Use projection helper to exclude _id field from output
  // collection.find().projection(excludeId()).first().printHeadResult()

  // log("ID excluded", log_suc)

  // //Use an Aggregate function to calculate i * 10
  // collection.aggregate(Seq(filter(gt("i", 0)),
  // project(Document("""{ITimes10: {$mulitplyP: ["$i""""))))

  // //Delete document with i 110
  // collection.deleteOne(equal("i", 110)).printHeadResult("Delete Result:")

  // //drop collection
  // collection.drop()

  // //drop database
  // db.drop()

  // val transactionOptions = TransactionOptions.builder()
  //       .readPreference(ReadPreference.primary())
  //       .readConcern(ReadConcern.SNAPSHOT)
  //       .writeConcern(WriteConcern.MAJORITY)
  //       .build()
  //     clientSession.startTransaction(transactionOptions)
  //     employeesCollection.updateOne(clientSession, Filters.eq("employee", 3), Updates.set("status", "Inactive"))
  //       .subscribe((res: UpdateResult) => println(res))
  //     eventsCollection.insertOne(clientSession, Document("employee" -> 3, "status" -> Document("new" -> "Inactive", "old" -> "Active")))
  //       .subscribe((res: Completed) => println(res))
  
  mongoClient.close()

  log("Client Closed", log_suc)
  }
}