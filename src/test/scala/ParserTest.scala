import java.io.File

import org.junit.Test

/**
  * Author: stk
  * Date: 18/4/6
  */
class ParserTest {
  val path = "C:\\Users\\stk\\Downloads\\test-data\\transport\\nio\\MockNioTransport.txt"
  val path1 = "C:\\Users\\stk\\Downloads\\test-data\\transport\\AbstractSimpleTransportTestCase.txt"

  @Test
  def parseFile(): Unit = {
    val result = FileParser.parseFile(new File(path1))
//    if (result != null) result.foreach(println)
  }
}
