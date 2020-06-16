import org.mongodb.scala.bson.ObjectId
import io.jvm.uuid._

object Building {
    def apply(address: String, is_published: Boolean, username_creator: String, coordinates_lat: Double, name: String, description: String, bucode: String, coordinates_lon: Double, url: String): Building = 
        Building(new ObjectId(), UUID.random, address, is_published, username_creator, coordinates_lat, name, description, bucode, coordinates_lon, url)
}

case class Building(_id: ObjectId, buid: UUID, address: String, is_published: Boolean, username_creator: String, coordinates_lat: Double, name: String, description: String, bucode: String, coordinates_lon: Double, url: String)