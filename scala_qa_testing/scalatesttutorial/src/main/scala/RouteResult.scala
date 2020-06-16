import org.mongodb.scala.bson.ObjectId
import io.jvm.uuid._

object RouteResult {
    def apply(start: Double, end: Double, path_info: String): RouteResult = 
        RouteResult(new ObjectId(), UUID.random, start, end, path_info)
}

case class RouteResult(_id: ObjectId, ruid: UUID, start: Double, end: Double, path_info: String)