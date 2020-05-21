/**
* def x = _x is a getter
* def x_ is the setter
*/

class PrivatePoint {
    private var _x = 0
    private var _y = 0
    private val bound = 100

    def x = _x
    def x_= (newValue: Int): Unit = {
        if (newValue < bound) _x = newValue else printWarning
    }

    def y = _y
    def y_= (newValue: Int): Unit = {
        if (newValue < bound) _y = newValue else printWarning
    }

    private def printWarning = println("WARNING: Out of bounds")
}