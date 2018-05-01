import java.io.{File, PrintWriter}

/**
  * Author: stk
  * Date: 18/4/6
  */
object Parser {
  def main(args: Array[String]): Unit = {
    val root = "C:\\Users\\stk\\Downloads\\test-data\\projects\\"
    val name = "3-elasticsearch"
    val projectPath = root + name
    val outputPath = root + name + ".txt"
    val files = getBlameFiles(new File(projectPath)).toVector
    generate(parseProject(files), outputPath)
  }

  def getBlameFiles(file: File): Array[File] = {
    val files = file.listFiles
    files.filter(_.isFile).filter(_.toString.endsWith(".blame")) ++ files.filter(_.isDirectory).flatMap(getBlameFiles)
  }

  def parseProject(files: Vector[File]): Vector[String] = files.par.map(FileParser.parseFile).reduce(_ ++ _)

  def generate(logs: Vector[String], path: String): Unit = {
    if (new File(path).delete) println("Delete existing result file.")
    val writer = new PrintWriter(new File(path))
    logs.foreach(s => writer.write(s + "\n"))
    writer.flush()
    writer.close()
  }
}
