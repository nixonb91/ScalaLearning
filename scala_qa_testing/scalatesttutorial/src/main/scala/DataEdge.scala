import org.mongodb.scala.bson.ObjectId
import io.jvm.uuid._

object DataEdge {
    def apply(label: String, weight: Double, source: String, target: String): DataEdge = 
        DataEdge(new ObjectId(), UUID.random, label, weight, source, target)
}

case class DataEdge(_id: ObjectId, deuid: UUID, label: String, weight: Double, source: String, target: String) {
    def setWeight(edgeWeight: Double): DataEdge = {
        val toReturn: DataEdge = this.copy(weight = edgeWeight)
        toReturn
    }
}