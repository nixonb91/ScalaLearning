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

class DataEdgeTest extends org.scalatest.funsuite.AnyFunSuite with BeforeAndAfterEach {

  //Values used for connections
  val user: String = "anyplace"
  val source: String = "caprioDB" //the source where the user is defined/stored
  val password: String= "nopass"
  val clusterName: String = "capriocluster"
  val uri: String = (s"mongodb+srv://$user:$password@$clusterName-ihd8s.gcp.mongodb.net/$source?retryWrites=true&w=majority")

  //class variables for testing, used across test cases
  var client : MongoClient = _
  var db : MongoDatabase = _
  var coll : MongoCollection[DataEdge] = _
  var edge : DataEdge = _
  val codecRegistry = fromRegistries(fromProviders(classOf[DataEdge]), DEFAULT_CODEC_REGISTRY)
  val rand: Random = Random
  var x: Double = _
  val list = Seq (
    DataEdge("Label One", 4.51, "A", "B"),
    DataEdge("Label Two", 21.33, "B", "C"),
    DataEdge("Label Three", 78.12, "C", "D")
  )

  //Startup Method
  override def beforeEach(): Unit = {
    // LoggerFactory.getLogger("org.mongodb.driver").asInstanceOf[Logger].setLevel(Level.WARN)

    //Establish Connection for MongoDB
    client = MongoClient(uri)
    db = client.getDatabase("testDataEdgeDB").withCodecRegistry(codecRegistry)
    coll = db.getCollection("testDataEdgeColl")
    x = rand.nextDouble

    edge = DataEdge("Test Label", x, "Source", "Target")
  }

  //Teardown Method
  override def afterEach(): Unit = {
    coll.drop().results()
    db.drop()
    client.close()
  }

  /**
  * This case tests inserting a DataEdge
  * into MongoDB.
  * Preconditions: Client Connection is established
  * and test DBs are ready for operations.
  * Execution Steps: coll.insertOne(edge)
  * Postconditions: DataEdge returned on query
  */
  test("DataEdge.insert") {
    coll.insertOne(edge).results()
    
    assert(coll.find(equal("label", edge.label)).first().headResult() === edge)
  }

  /**
  * This case tests inserting many DataEdges into MongoDB.
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and a list of
  * DataEdges is created.
  * Execution Steps: coll.insertMany(list)
  * PostConditions: All DataEdges returned from query
  */
  test("DataEdge.insertMany") {
    coll.insertMany(list).results()

    for (litem <- list) {
      assert(coll.find(equal("label", litem.label)).first().headResult() === litem)
    }
  }

  /**
  * This case tests updating one DataEdge in MongoDB.
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and edge is inserted
  * into testDB.
  * Execution Steps: coll.updateOne(equal("start", edge.start), set("path_info", "updated->path"))
  * PostConditions: Queried DataEdge has updated path_info
  */
  test("DataEdge.updateOne") {
    coll.insertOne(edge).results()

    coll.updateOne(equal("label", edge.label), set("source", "ADMT Lab")).results()

    val queried = coll.find(equal("label", edge.label)).first().headResult()

    assert(queried.label === edge.label)
    assert(queried.weight === edge.weight)
    assert(queried.deuid === edge.deuid)
    assert(queried.target === edge.target)
    assert(queried.source === "ADMT Lab")
  }

  /**
  * This case tests updating many DataEdges in MongoDB.
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and list is inserted
  * into testDB.
  * Execution Steps: coll.updateMany(regex("path_info", "^L"), set("path_info", "many updated->paths"))
  * PostConditions: Queried DataEdges has updated path_info
  */
  test("DataEdge.updateMany") {
    coll.insertMany(list).results()

    coll.updateMany(regex("label", "^L"), set("target", "Athens")).results()

    //returns a sequence of DataEdges
    for (litem <- list) {
        val queried = coll.find(equal("label", litem.label)).headResult()

        assert(queried.label === litem.label)
        assert(queried.weight === litem.weight)
        assert(queried.deuid === litem.deuid)
        assert(queried.source === litem.source)
        assert(queried.target === "Athens")
    }
  }

  /**
  * This case tests getting the max start and max end values for
  * DataEdges in MongoDB.
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and list is inserted
  * into testDB.
  * Execution Steps: coll.find().sort(orderBy(descending("start"), descending("end"))).first().headResult()
  * PostConditions: List Item Three is returned by Query
  */
  test("DataEdge.MaxSort") {
    coll.insertMany(list).results()

    val queried = coll.find().sort(orderBy(descending("weight"))).first().headResult()

    assert(queried === list(2))
  }

   /**
  * This case tests getting the min start and min end values for
  * DataEdges in MongoDB.
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and list is inserted
  * into testDB.
  * Execution Steps: coll.find().sort(orderBy(ascending("start"), ascending("end"))).first().headResult()
  * PostConditions: List Item Three is returned by Query
  */
  test("DataEdge.MinSort") {
    coll.insertMany(list).results()

    val queried = coll.find().sort(orderBy(ascending("weight"))).first().headResult()

    assert(queried === list(0))
  }

  /**
  * This case tests deleting a single DataEdge from MongoDB
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and edge is inserted
  * into testDB.
  * Execution Steps: coll.deleteOne(equal("path_info", edge.path_info)).results()
  * PostConditions: coll.find(equal("path_info", edge.path_info)).first().headResult() returns null
  */
  test("DataEdge.DeleteOne") {
    coll.insertOne(edge).results()

    coll.deleteOne(equal("label", edge.label)).results()

    assert(coll.find(equal("label", edge.label)).first().headResult() === null)
  }

  /**
  * This case tests deleting many DataEdge from MongoDB
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and list is inserted
  * into testDB.
  * Execution Steps: coll.deleteMany(regex("path_info", "^L")).results()
  * PostConditions: coll.find().first().headResult() returns null
  */
  test("DataEdge.DeleteMany") {
    coll.insertMany(list).results()

    coll.deleteMany(regex("label", "^L")).results()

    assert(coll.find().first().headResult() === null)
  }

  //===========================================
  //==========Test Edge Setters============
  //===========================================

  test("DataEdge.setWeight") {
    val edgeWeight: Double = 0.78
    val updatedEdge: DataEdge = edge.setWeight(edgeWeight)

    assert(updatedEdge._id === edge._id)
    assert(updatedEdge.deuid === edge.deuid)
    assert(updatedEdge.label === edge.label)
    assert(updatedEdge.weight === edgeWeight)
    assert(updatedEdge.source === edge.source)
    assert(updatedEdge.target === edge.target)
  }
}
