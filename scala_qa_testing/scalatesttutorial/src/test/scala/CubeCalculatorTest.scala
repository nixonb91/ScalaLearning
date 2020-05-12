import org.scalatest.FunSuite

/**
* Creates the CubeCalculator Test class with test case methods inside
*/

class CubeCalculatorTest extends org.scalatest.FunSuite {
  //CubeCalculator.cube is the name of the test case
  test("CubeCalculator.cube") {
    assert(CubeCalculator.cube(3) === 27)
  }

  test("CubeCalculator.cube zero cubed") {
    assert(CubeCalculator.cube(0) === 0)
  }

  test("CubeCalculator.cube -1 is -1") {
    assert(CubeCalculator.cube(-1) === -1)
  }
}
