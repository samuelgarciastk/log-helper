import java.io.File

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.stmt.ExpressionStmt
import com.github.javaparser.ast.visitor.VoidVisitorAdapter

import scala.collection.mutable
import scala.io.Source

/**
  * Author: stk
  * Date: 18/4/6
  */
object FileParser {
  private val regex = "^\\w{11}\\s*(.*?)\\s*\\((.*?)\\s+\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} .\\d{4}\\s+(\\d+)\\) (.*)$".r

  def parseFile(file: File): List[String] = {
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
    val compilationUnit = JavaParser.parse(code)
    if (compilationUnit == null) return null
    val infoMap: mutable.HashMap[Int, mutable.ListBuffer[String]] = mutable.HashMap()
    compilationUnit.accept(new LogCollector, infoMap)
    if (infoMap.isEmpty) return null

    // Merge nameMap and infoMap
    infoMap.foreach { case (index, list) => list.prepend(nameMap(index)) }

//    infoMap.foreach { case (index, list) => println(index + ": " + list.head) }
    null
  }

  class LogCollector extends VoidVisitorAdapter[mutable.HashMap[Int, mutable.ListBuffer[String]]] {
    private val patterns: List[String] = {
      val source = Source.fromResource("pattern")
      val list = source.getLines.toList
      source.close()
      list
    }

    override def visit(n: ExpressionStmt, arg: mutable.HashMap[Int, mutable.ListBuffer[String]]): Unit = {
      super.visit(n, arg)
      if (patterns.par.map(_.r.findFirstIn(n.toString)).exists(_.isDefined)) {
        arg += (n.getBegin.get.line -> mutable.ListBuffer(n.toString))
      }
    }
  }

}
