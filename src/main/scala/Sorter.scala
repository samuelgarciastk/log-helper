import java.io.{File, PrintWriter}

import scala.collection.mutable
import scala.io.Source

/**
  * Author: stk
  * Date: 18/5/19
  */
object Sorter {
  val root = "C:\\Users\\stk\\Documents\\Docs\\Papers\\ESEM2018Log\\data\\"

  def main(args: Array[String]): Unit = {
    val files = getFiles(new File(root)).toVector
    files.par.foreach(sort)
    files.par.foreach(sortProject)
  }

  def getFiles(file: File): Array[File] = {
    val pattern = """\d{1,3}-.*(?<!-stat|-proj)\.txt""".r
    val files = file.listFiles
    files.filter(_.isFile).filter(p => pattern.findFirstIn(p.toString).isDefined)
  }

  def sort(file: File): Unit = {
    val output = "(.*)\\.txt".r.findFirstMatchIn(file.getName).get.group(1) + "-stat.txt"
    val source = Source.fromFile(file)
    val lines = source.getLines.toVector
    source.close()
    if (lines.isEmpty) return

    val locMap = new mutable.HashMap[String, Int]()
    val levelMap = new mutable.HashMap[String, Int]()
    val typeMap = new mutable.HashMap[String, Int]()

    lines.foreach(f => {
      val data = f.split(":", 2)(1).split(",")
      val name = data(0).trim
      val level = name + "," + data(1).trim.toLowerCase
      val category = name + "," + data(2).trim
      if (locMap.contains(name))
        locMap += (name -> (locMap(name) + 1))
      else
        locMap += (name -> 1)
      if (levelMap.contains(level))
        levelMap += (level -> (levelMap(level) + 1))
      else
        levelMap += (level -> 1)
      if (typeMap.contains(category))
        typeMap += (category -> (typeMap(category) + 1))
      else
        typeMap += (category -> 1)
    })

    val locSeq = locMap.toSeq.sortBy(_._2).reverse
    val levelSeq = levelMap.toSeq.sortBy(f => (f._1.split(",")(0), f._2)).reverse
    val typeSeq = typeMap.toSeq.sortBy(f => (f._1.split(",")(0), f._2)).reverse

    val writer = new PrintWriter(new File(root + output))
    writer.write(delimiter("# Logging Statement"))
    locSeq.foreach(f => writer.write("%-10d %s\n".format(f._2, f._1)))
    writer.write(delimiter("Log Level"))
    levelSeq.foreach(f => writer.write("%-10d %s\n".format(f._2, f._1)))
    writer.write(delimiter("Context"))
    typeSeq.foreach(f => writer.write("%-10d %s\n".format(f._2, f._1)))
    writer.flush()
    writer.close()
  }

  def delimiter(msg: String): String = {
    val length = msg.length + 4
    var delimiter = ""
    for (_ <- 1 to length) yield delimiter += "="
    s"\n$delimiter\n= $msg =\n$delimiter\n\n"
  }

  def sortProject(file: File): Unit = {
    val output = "(.*)\\.txt".r.findFirstMatchIn(file.getName).get.group(1) + "-proj.txt"
    val source = Source.fromFile(file)
    val lines = source.getLines.toVector
    source.close()
    if (lines.isEmpty) return

    var loc = 0
    val levelMap = new mutable.HashMap[String, Int]()
    val typeMap = new mutable.HashMap[String, Int]()

    lines.foreach(f => {
      val data = f.split(":", 2)(1).split(",")
      val level = data(1).trim.toLowerCase
      val category = data(2).trim
      loc += 1
      if (levelMap.contains(level))
        levelMap += (level -> (levelMap(level) + 1))
      else
        levelMap += (level -> 1)
      if (typeMap.contains(category))
        typeMap += (category -> (typeMap(category) + 1))
      else
        typeMap += (category -> 1)
    })

    val levelSeq = levelMap.toSeq.sortBy(_._2).reverse
    val typeSeq = typeMap.toSeq.sortBy(_._2).reverse

    val writer = new PrintWriter(new File(root + output))
    writer.write(delimiter("# Logging Statement"))
    writer.write(loc + "\n")
    writer.write(delimiter("Log Level"))
    levelSeq.foreach(f => writer.write("%-10d %s\n".format(f._2, f._1)))
    writer.write(delimiter("Context"))
    typeSeq.foreach(f => writer.write("%-10d %s\n".format(f._2, f._1)))
    writer.flush()
    writer.close()
  }
}
