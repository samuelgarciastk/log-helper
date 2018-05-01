import java.io.File

import org.junit.Test

/**
  * Author: stk
  * Date: 18/4/6
  */
class ParserTest {
  val path = "C:\\Users\\stk\\Downloads\\test-data\\projects\\26-netty\\buffer\\src\\main\\java\\io\\netty\\buffer\\AbstractByteBuf.blame"

  @Test
  def parseFile(): Unit = {
    val result = FileParser.parseFile(new File(path))
    if (result != null) result.foreach(println)
  }
}
