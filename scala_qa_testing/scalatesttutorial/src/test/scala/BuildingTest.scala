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
import io.jvm.uuid._

import tour.Helpers._


/**
* Creates the Main Test class with test case methods inside
*/

class BuildingTest extends org.scalatest.funsuite.AnyFunSuite with BeforeAndAfterEach {

  //Values used for connections
  val user: String = "anyplace"
  val source: String = "caprioDB" //the source where the user is defined/stored
  val password: String= "nopass"
  val clusterName: String = "capriocluster"
  val uri: String = (s"mongodb+srv://$user:$password@$clusterName-ihd8s.gcp.mongodb.net/$source?retryWrites=true&w=majority")

  //class variables for testing, used across test cases
  var client : MongoClient = _
  var db : MongoDatabase = _
  var coll : MongoCollection[Building] = _
  var building : Building = _
  val codecRegistry = fromRegistries(fromProviders(classOf[Building]), DEFAULT_CODEC_REGISTRY)
  val rand: Random = Random
  var x: Double = _
  var y: Double = _
  val list = Seq (
    Building("Cyprus Street", true, "Costa", 101.25, "Some Keo Beer Company", "A simple Cyprus themed Building", "Code-Alpha", 84.31, "cyprus.gov"),
    Building("Squash Ave", false, "Rakan", 20.14, "Squash Indoor Field", "A squach themed Building", "Code-Squash", 31.98, "squash.com"),
    Building("Bouquet Ave", true, "Brian", 71.25, "Sennott Square", "Computer Science Department", "SIG-SEGFAULT", 61.46, "cs.pitt.edu")
  )

  //Startup Method
  override def beforeEach(): Unit = {
    LoggerFactory.getLogger("org.mongodb.driver").asInstanceOf[Logger].setLevel(Level.WARN)

    //Establish Connection for MongoDB
    client = MongoClient(uri)
    db = client.getDatabase("testBuildingDB").withCodecRegistry(codecRegistry)
    coll = db.getCollection("testBuildingColl")
    x = rand.nextDouble
    y = rand.nextDouble

    building = Building("ABC Street", true, "Don Knuth", x, "Caprio Building", "The Art of Computer Programming", "overfull hspace", y, "stackoverflow.com/latex-help")
  }

  //Teardown Method
  override def afterEach(): Unit = {
    coll.drop().results()
    db.drop()
    client.close()
  }

  /**
  * This case tests inserting a Building
  * into MongoDB.
  * Preconditions: Client Connection is established
  * and test DBs are ready for operations.
  * Execution Steps: coll.insertOne(building)
  * Postconditions: Building returned on query
  */
  test("Building.insert") {
    coll.insertOne(building).results()
    
    assert(coll.find(equal("name", building.name)).first().headResult() === building)
  }

  /**
  * This case tests inserting many Buildings into MongoDB.
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and a list of
  * Buildings is created.
  * Execution Steps: coll.insertMany(list)
  * PostConditions: All Buildings returned from query
  */
  test("Building.insertMany") {
    coll.insertMany(list).results()

    for (litem <- list) {
      assert(coll.find(equal("name", litem.name)).first().headResult() === litem)
    }
  }

  /**
  * This case tests updating one Building in MongoDB.
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and building is inserted
  * into testDB.
  * Execution Steps: coll.updateOne(equal("start", building.start), set("path_info", "updated->path"))
  * PostConditions: Queried Building has updated path_info
  */
  test("Building.updateOne") {
    coll.insertOne(building).results()

    coll.updateOne(equal("name", building.name), set("address", "Algorithm Lane")).results()

    val queried = coll.find(equal("name", building.name)).first().headResult()

    assert(queried.address === "Algorithm Lane")
    assert(queried.is_published === building.is_published)
    assert(queried.username_creator === building.username_creator)
    assert(queried.coordinates_lat === building.coordinates_lat)
    assert(queried.description === building.description)
    assert(queried.name === building.name)
    assert(queried.bucode === building.bucode)
    assert(queried.coordinates_lon === building.coordinates_lon)
    assert(queried.url === building.url)
    assert(queried.buid === building.buid)
  }

  /**
  * This case tests updating many Buildings in MongoDB.
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and list is inserted
  * into testDB.
  * Execution Steps: coll.updateMany(regex("path_info", "^L"), set("path_info", "many updated->paths"))
  * PostConditions: Queried Buildings has updated path_info
  */
  test("Building.updateMany") {
    coll.insertMany(list).results()

    coll.updateMany(regex("name", "^S"), set("name", "Updated Name")).results()

    //returns a sequence of Buildings
    for (litem <- list) {
        val queried = coll.find(equal("address", litem.address)).headResult()

        assert(queried.address === litem.address)
        assert(queried.is_published === litem.is_published)
        assert(queried.username_creator === litem.username_creator)
        assert(queried.coordinates_lat === litem.coordinates_lat)
        assert(queried.description === litem.description)
        assert(queried.name === "Updated Name")
        assert(queried.bucode === litem.bucode)
        assert(queried.coordinates_lon === litem.coordinates_lon)
        assert(queried.url === litem.url)
        assert(queried.buid === litem.buid)
    }
  }

  /**
  * This case tests getting the max start and max end values for
  * Buildings in MongoDB.
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and list is inserted
  * into testDB.
  * Execution Steps: coll.find().sort(orderBy(descending("start"), descending("end"))).first().headResult()
  * PostConditions: List Item Three is returned by Query
  */
  test("Building.MaxSort") {
    coll.insertMany(list).results()

    val queried = coll.find().sort(orderBy(descending("coordinate_lon"), descending("coordinate_lat"))).first().headResult()

    assert(queried === list(0))
  }

   /**
  * This case tests getting the min start and min end values for
  * Buildings in MongoDB.
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and list is inserted
  * into testDB.
  * Execution Steps: coll.find().sort(orderBy(ascending("start"), ascending("end"))).first().headResult()
  * PostConditions: List Item Three is returned by Query
  */
  test("Building.MinSort") {
    coll.insertMany(list).results()

    val queried = coll.find().sort(orderBy(ascending("coordinates_lon"), ascending("coordinates_lat"))).first().headResult()

    assert(queried === list(1))
  }

  /**
  * This case tests deleting a single Building from MongoDB
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and building is inserted
  * into testDB.
  * Execution Steps: coll.deleteOne(equal("path_info", building.path_info)).results()
  * PostConditions: coll.find(equal("path_info", building.path_info)).first().headResult() returns null
  */
  test("Building.DeleteOne") {
    coll.insertOne(building).results()

    coll.deleteOne(equal("name", building.name)).results()

    assert(coll.find(equal("name", building.name)).first().headResult() === null)
  }

  /**
  * This case tests deleting many Building from MongoDB
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and list is inserted
  * into testDB.
  * Execution Steps: coll.deleteMany(regex("path_info", "^L")).results()
  * PostConditions: coll.find().first().headResult() returns null
  */
  test("Building.DeleteMany") {
    coll.insertMany(list).results()

    coll.deleteMany(regex("name", "^S")).results()

    assert(coll.find().first().headResult() === null)
  }

}
