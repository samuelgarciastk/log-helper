import scala.io.Source

/**
  * Author: stk
  * Date: 18/4/21
  */
object LogPattern {
  val patterns: Vector[String] = {
    val source = Source.fromResource("pattern")
    val list = source.getLines.toVector
    source.close()
    list
  }
}
