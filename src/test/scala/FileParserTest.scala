import java.io.File

import org.junit.Test

/**
  * Author: stk
  * Date: 18/4/6
  */
class FileParserTest {
  @Test
  def parseFile(): Unit = {
    val file = "C:\\Users\\stk\\Documents\\Docs\\Papers\\EASE2018Log\\Data\\elasticsearch\\test\\framework\\src\\main\\java\\org\\elasticsearch\\transport\\MockTcpTransport.java"
    FileParser.parseFile(new File(file)).foreach(println)
  }
}
