import scala.util.{Try, Success, Failure}
import com.github.t3hnar.bcrypt._

object UsableBcrypt {
    private val saltSize: Int = 12
    def hashAndSalt(str: String): String = str.bcrypt(saltSize)
    private def checkHash(raw: String, hashed: String): Try[Boolean] = raw.isBcryptedSafe(hashed)

    def verifyHash(raw: String, hashed: String): Boolean = {
        val verifyResult = checkHash(raw, hashed) match {
            case Success(_) => true
            case Failure(f) => false
        }

        verifyResult
    }
}