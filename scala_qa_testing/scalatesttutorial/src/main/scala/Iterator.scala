// A generic Iterator class

trait Iterator[A] {
    //define abstract methods
    def hasNext: Boolean
    def next: A
}