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

class DataNodeTest extends org.scalatest.funsuite.AnyFunSuite with BeforeAndAfterEach {

  //Values used for connections
  val user: String = "anyplace"
  val source: String = "caprioDB" //the source where the user is defined/stored
  val password: String= "nopass"
  val clusterName: String = "capriocluster"
  val uri: String = (s"mongodb+srv://$user:$password@$clusterName-ihd8s.gcp.mongodb.net/$source?retryWrites=true&w=majority")

  //class variables for testing, used across test cases
  var client : MongoClient = _
  var db : MongoDatabase = _
  var coll : MongoCollection[DataNode] = _
  var node : DataNode = _
  val codecRegistry = fromRegistries(fromProviders(classOf[DataNode]), DEFAULT_CODEC_REGISTRY)
  val list = Seq (
    DataNode("Node One"),
    DataNode("Node Two"),
    DataNode("Node Three")
  )

  //Startup Method
  override def beforeEach(): Unit = {
    // LoggerFactory.getLogger("org.mongodb.driver").asInstanceOf[Logger].setLevel(Level.WARN)

    //Establish Connection for MongoDB
    client = MongoClient(uri)
    db = client.getDatabase("testDataNodeDB").withCodecRegistry(codecRegistry)
    coll = db.getCollection("testDataNodeColl")

    node = DataNode("Test Node")
  }

  //Teardown Method
  override def afterEach(): Unit = {
    coll.drop().results()
    db.drop()
    client.close()
  }

  /**
  * This case tests inserting a DataNode
  * into MongoDB.
  * Preconditions: Client Connection is established
  * and test DBs are ready for operations.
  * Execution Steps: coll.insertOne(node)
  * Postconditions: DataNode returned on query
  */
  test("DataNode.insert") {
    coll.insertOne(node).results()
    
    assert(coll.find(equal("name", node.name)).first().headResult() === node)
  }

  /**
  * This case tests inserting many DataNodes into MongoDB.
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and a list of
  * DataNodes is created.
  * Execution Steps: coll.insertMany(list)
  * PostConditions: All DataNodes returned from query
  */
  test("DataNode.insertMany") {
    coll.insertMany(list).results()

    for (litem <- list) {
      assert(coll.find(equal("name", litem.name)).first().headResult() === litem)
    }
  }

  /**
  * This case tests updating one DataNode in MongoDB.
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and node is inserted
  * into testDB.
  * Execution Steps: coll.updateOne(equal("start", node.start), set("path_info", "updated->path"))
  * PostConditions: Queried DataNode has updated path_info
  */
  test("DataNode.updateOne") {
    coll.insertOne(node).results()

    coll.updateOne(equal("name", node.name), set("name", "ADMT Node")).results()

    val queried = coll.find(equal("dnuid", node.dnuid)).first().headResult()

    assert(queried.name === "ADMT Node")
    assert(queried.dnuid === node.dnuid)
  }

  /**
  * This case tests updating many DataNodes in MongoDB.
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and list is inserted
  * into testDB.
  * Execution Steps: coll.updateMany(regex("path_info", "^N"), set("path_info", "many updated->paths"))
  * PostConditions: Queried DataNodes has updated path_info
  */
  test("DataNode.updateMany") {
    coll.insertMany(list).results()

    coll.updateMany(regex("name", "^N"), set("name", "Pittsburgh Node")).results()

    //returns a sequence of DataNodes
    for (litem <- list) {
        val queried = coll.find(equal("dnuid", litem.dnuid)).headResult()

        assert(queried.name === "Pittsburgh Node")
        assert(queried.dnuid === litem.dnuid)
    }
  }

  /**
  * This case tests getting the max start and max end values for
  * DataNodes in MongoDB.
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and list is inserted
  * into testDB.
  * Execution Steps: coll.find().sort(orderBy(descending("start"), descending("end"))).first().headResult()
  * PostConditions: List Item Three is returned by Query
  */
  test("DataNode.MaxSort") {
    coll.insertMany(list).results()

    val queried = coll.find().sort(orderBy(descending("name"))).first().headResult()

    assert(queried === list(1)) //Using lexicographical max
  }

   /**
  * This case tests getting the min start and min end values for
  * DataNodes in MongoDB.
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and list is inserted
  * into testDB.
  * Execution Steps: coll.find().sort(orderBy(ascending("start"), ascending("end"))).first().headResult()
  * PostConditions: List Item Three is returned by Query
  */
  test("DataNode.MinSort") {
    coll.insertMany(list).results()

    val queried = coll.find().sort(orderBy(ascending("name"))).first().headResult()

    assert(queried === list(0)) //Using lexicographical min
  }

  /**
  * This case tests deleting a single DataNode from MongoDB
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and node is inserted
  * into testDB.
  * Execution Steps: coll.deleteOne(equal("path_info", node.path_info)).results()
  * PostConditions: coll.find(equal("path_info", node.path_info)).first().headResult() returns null
  */
  test("DataNode.DeleteOne") {
    coll.insertOne(node).results()

    coll.deleteOne(equal("name", node.name)).results()

    assert(coll.find(equal("name", node.name)).first().headResult() === null)
  }

  /**
  * This case tests deleting many DataNode from MongoDB
  * Preconditions: Client Connection is established,
  * test DBs are ready for operations, and list is inserted
  * into testDB.
  * Execution Steps: coll.deleteMany(regex("path_info", "^N")).results()
  * PostConditions: coll.find().first().headResult() returns null
  */
  test("DataNode.DeleteMany") {
    coll.insertMany(list).results()

    coll.deleteMany(regex("name", "^N")).results()

    assert(coll.find().first().headResult() === null)
  }
}
