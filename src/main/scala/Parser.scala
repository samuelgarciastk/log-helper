import java.io.{File, PrintWriter}
import java.util.concurrent.ConcurrentHashMap

import scala.collection.mutable
import scala.io.Source

/**
  * Author: stk
  * Date: 18/4/6
  */
object Parser {
  val root = "F:\\GitHub Data\\"
  val name = "100-hadoop"

  def main(args: Array[String]): Unit = {
    val projectPath = root + name
    val outputPath = root + name + ".txt"
    val files = getBlameFiles(new File(projectPath)).toVector
    //    generate(parseProject(files), outputPath)
    sortProject(files)
  }

  def getBlameFiles(file: File): Array[File] = {
    val files = file.listFiles
    files.filter(_.isFile).filter(_.toString.endsWith(".blame")) ++ files.filter(_.isDirectory).flatMap(getBlameFiles)
  }

  def parseProject(files: Vector[File]): Vector[String] = files.par.map(FileParser.parseFile).reduce(_ ++ _)

  def sortProject(files: Vector[File]): Unit = {
    val container = new ConcurrentHashMap[String, Int]()
    val seq = files.par.map(sort).reduce((m1, m2) => m1 ++ m2.map { case (k, v) => k -> (v + m1.getOrElse(k, 0)) }).toSeq.sortBy(_._2).reverse
    val writer = new PrintWriter(new File(root + name + "-loc.txt"))
    seq.foreach(f => writer.write("%-10d\t%s\n".format(f._2, f._1)))
    writer.flush()
    writer.close()
  }

  def sort(file: File): mutable.HashMap[String, Int] = {
    println("Sort file: " + file)
    val regex = "^[\\^\\w]+\\s*(.*)\\s*\\((.*)\\s+\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} .\\d{4}\\s+(\\d+)\\) (.*)$".r
    val source = Source.fromFile(file)
    val lines = source.getLines
    val map = new mutable.HashMap[String, Int]()
    lines.foreach(f => {
      regex.findFirstMatchIn(f).foreach(p => {
        val name = p.group(2).trim
        if (map.contains(name))
          map += (name -> (map(name) + 1))
        else
          map += (name -> 1)
      })
    })
    source.close()
    map
  }

  def generate(logs: Vector[String], path: String): Unit = {
    if (new File(path).delete) println("Delete existing result file.")
    val writer = new PrintWriter(new File(path))
    logs.foreach(s => writer.write(s + "\n"))
    writer.flush()
    writer.close()
  }
}
