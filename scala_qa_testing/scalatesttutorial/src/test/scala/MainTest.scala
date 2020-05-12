import org.scalatest.FunSuite

/**
* Creates the Main Test class with test case methods inside
*/

class MainTest extends org.scalatest.FunSuite {
  //Main.cube is the name of the test case
  test("Main.cube") {
    assert(Main.cube(3) === 27)
  }

  test("Main.cube zero cubed") {
    assert(Main.cube(0) === 0)
  }

  test("Main.cube -1 is -1") {
    assert(Main.cube(-1) === -1)
  }
}
