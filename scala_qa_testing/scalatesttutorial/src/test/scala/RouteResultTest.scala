import org.scalatest._

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
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromRegistries, fromProviders}
import ch.qos.logback.classic.{Level, Logger}
import org.slf4j.LoggerFactory
import scala.util.Random

import tour.Helpers._


/**
* Creates the Main Test class with test case methods inside
*/

class RouteResultTest extends org.scalatest.funsuite.AnyFunSuite with BeforeAndAfterEach {

  //Values used for connections
  val user: String = "anyplace"
  val source: String = "caprioDB" //the source where the user is defined/stored
  val password: String= "nopass"
  val clusterName: String = "capriocluster"
  val uri: String = (s"mongodb+srv://$user:$password@$clusterName-ihd8s.gcp.mongodb.net/$source?retryWrites=true&w=majority")

  //class variables for testing, used across test cases
  var client : MongoClient = _
  var db : MongoDatabase = _
  var coll : MongoCollection[RouteResult] = _
  var rr : RouteResult = _
  val codecRegistry = fromRegistries(fromProviders(classOf[RouteResult]), DEFAULT_CODEC_REGISTRY)
  val rand: Random = Random
  var x: Double = _
  var y: Double = _
  val list = Seq (
    RouteResult(1, 1, "List Item One"),
    RouteResult(2, 2, "List Item Two"),
    RouteResult(3, 3, "List Item Three")
  )

  //Startup Method
  override def beforeEach(): Unit = {
    // LoggerFactory.getLogger("org.mongodb.driver").asInstanceOf[Logger].setLevel(Level.WARN)

    //Establish Connection for MongoDB
    client = MongoClient(uri)
    db = client.getDatabase("testRouteDB").withCodecRegistry(codecRegistry)
    coll = db.getCollection("testRouteColl")
    x = rand.nextDouble
    y = rand.nextDouble

    rr = RouteResult(x, y, "source->testing->sink")
  }

  //Teardown Method
  override def afterEach(): Unit = {
    coll.drop().results()
    db.drop()
    client.close()
  }

  /**
  * This case tests inserting a RouteResult
  * into MongoDB.
  * Preconditions: Client Connection is established
  * and test DBs are ready for operations.
  * Execution Steps: coll.insertOne(rr)
  * Postconditions: RouteResult returned on query
  */
  test("RouteResult.insert") {
    coll.insertOne(rr).results()
    
    assert(coll.find(equal("start", rr.start)).first().headResult() === rr)
  }

  /**
  * This case tests inserting many RouteResults into MongoDB.
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and a list of
  * RouteResults is created.
  * Execution Steps: coll.insertMany(list)
  * PostConditions: All RouteResults returned from query
  */
  test("RouteResult.insertMany") {
    coll.insertMany(list).results()

    for (litem <- list) {
      assert(coll.find(equal("start", litem.start)).first().headResult() === litem)
    }
  }

  /**
  * This case tests updating one RouteResult in MongoDB.
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and rr is inserted
  * into testDB.
  * Execution Steps: coll.updateOne(equal("start", rr.start), set("path_info", "updated->path"))
  * PostConditions: Queried RouteResult has updated path_info
  */
  test("RouteResult.updateOne") {
    coll.insertOne(rr).results()

    coll.updateOne(equal("start", rr.start), set("path_info", "updated->path")).results()

    val queried = coll.find(equal("start", rr.start)).first().headResult()

    assert(queried.start === rr.start)
    assert(queried.end === rr.end)
    assert(queried.path_info === "updated->path")
  }

  /**
  * This case tests updating many RouteResults in MongoDB.
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and list is inserted
  * into testDB.
  * Execution Steps: coll.updateMany(regex("path_info", "^L"), set("path_info", "many updated->paths"))
  * PostConditions: Queried RouteResults has updated path_info
  */
  test("RouteResult.updateMany") {
    coll.insertMany(list).results()

    coll.updateMany(regex("path_info", "^L"), set("path_info", "many updated->paths")).results()

    //returns a sequence of RouteResults
    for (litem <- list) {
      val queried = coll.find(equal("start", litem.start)).headResult()

      assert(queried.start === litem.start)
      assert(queried.end === litem.end)
      assert(queried.path_info === "many updated->paths")
    }
  }

  /**
  * This case tests getting the max start and max end values for
  * RouteResults in MongoDB.
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and list is inserted
  * into testDB.
  * Execution Steps: coll.find().sort(orderBy(descending("start"), descending("end"))).first().headResult()
  * PostConditions: List Item Three is returned by Query
  */
  test("RouteResult.MaxSort") {
    coll.insertMany(list).results()

    val queried = coll.find().sort(orderBy(descending("start"), descending("end"))).first().headResult()

    assert(queried === list(2))
  }

   /**
  * This case tests getting the min start and min end values for
  * RouteResults in MongoDB.
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and list is inserted
  * into testDB.
  * Execution Steps: coll.find().sort(orderBy(ascending("start"), ascending("end"))).first().headResult()
  * PostConditions: List Item Three is returned by Query
  */
  test("RouteResult.MinSort") {
    coll.insertMany(list).results()

    val queried = coll.find().sort(orderBy(ascending("start"), ascending("end"))).first().headResult()

    assert(queried === list(0))
  }

  /**
  * This case tests deleting a single RouteResult from MongoDB
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and rr is inserted
  * into testDB.
  * Execution Steps: coll.deleteOne(equal("path_info", rr.path_info)).results()
  * PostConditions: coll.find(equal("path_info", rr.path_info)).first().headResult() returns null
  */
  test("RouteResult.DeleteOne") {
    coll.insertOne(rr).results()

    coll.deleteOne(equal("path_info", rr.path_info)).results()

    assert(coll.find(equal("path_info", rr.path_info)).first().headResult() === null)
  }

  /**
  * This case tests deleting many RouteResult from MongoDB
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and list is inserted
  * into testDB.
  * Execution Steps: coll.deleteMany(regex("path_info", "^L")).results()
  * PostConditions: coll.find().first().headResult() returns null
  */
  test("RouteResult.DeleteMany") {
    coll.insertMany(list).results()

    coll.deleteMany(regex("path_info", "^L")).results()

    assert(coll.find().first().headResult() === null)
  }
}
