import java.io.{File, PrintWriter}

/**
  * Author: stk
  * Date: 18/4/6
  */
object Parser {
  def main(args: Array[String]): Unit = {
    val projectPath = "C:\\Users\\stk\\Downloads\\test-data\\transport"
    val outputPath = "C:\\Users\\stk\\Downloads\\test-data\\result.txt"
    getFiles(new File(projectPath)).foreach(println)
  }

  def getFiles(file: File): Array[File] = {
    val files = file.listFiles
    files.filter(_.isFile) ++ files.filter(_.isDirectory).flatMap(getFiles)
  }

  def parseProject(files: List[File]): List[String] = files.par.map(FileParser.parseFile).reduce(_ ++ _)

  def generate(logs: List[String], path: String): Unit = {
    if (new File(path).delete) println("Delete existing result file.")
    val writer = new PrintWriter(new File(path))
    logs.foreach(s => writer.write(s + "\n"))
    writer.flush()
    writer.close()
  }
}
