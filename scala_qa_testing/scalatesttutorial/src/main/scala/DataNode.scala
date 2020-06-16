import org.mongodb.scala.bson.ObjectId
import io.jvm.uuid._

object DataNode {
    def apply(name: String): DataNode = 
        DataNode(new ObjectId(), UUID.random, name)
}

case class DataNode(_id: ObjectId, dnuid: UUID, name: String)