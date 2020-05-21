//Create a Point Class with (0, 0) as the default case

class Point(var x: Int = 0, var  y: Int = 0) {
    //method to move the point
    def move(dx: Int, dy: Int): Unit = {
        x += dx
        y += dy
    }

    override def toString: String = s"($x, $y)"
}