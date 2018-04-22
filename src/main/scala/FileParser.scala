import java.io.File

import scala.collection.mutable
import scala.io.Source

/**
  * Author: stk
  * Date: 18/4/6
  */
object FileParser {
  private val regex = "^\\w{11}\\s*(.*?)\\s*\\((.*?)\\s+\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} .\\d{4}\\s+(\\d+)\\) (.*)$".r

  def parseFile(file: File): Vector[String] = {
    println("Parse " + file)

    // Parse git blame file
    val nameMap: mutable.HashMap[Int, String] = mutable.HashMap()
    val source = Source.fromFile(file)
    var code: String = ""
    source.getLines.foreach(f => regex.findAllIn(f).matchData.foreach(p => {
      nameMap += (p.group(3).toInt -> p.group(2))
      code += p.group(4) + "\n"
    }))
    source.close()

    // Parse AST
    val infoMap = ASTParser.parseAST(code)
    if (infoMap == null) return null

    // Merge nameMap and infoMap
    infoMap.foreach { case (index, list) => list.prepend(nameMap(index)) }

    infoMap.foreach{ case (index, list) => println(index + ": " + list.mkString(", "))}

    var result: Vector[String] = Vector()
    infoMap.foreach { case (_, list) => result = result :+ list.mkString(", ") }
    result
  }
}
