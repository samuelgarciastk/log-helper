import java.io.File

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.stmt.ExpressionStmt
import com.github.javaparser.ast.visitor.VoidVisitorAdapter

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Author: stk
  * Date: 18/4/6
  */
object FileParser {
  private val regex = "^\\w{11} (.*?\\.java)\\s+\\((.*?)\\s+\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} .\\d{4}\\s+(\\d+)\\) (.*)$".r

  def parseFile(file: File): List[String] = {
    println("Parse " + file)
    val nameMap: mutable.LinkedHashMap[Int, String] = mutable.LinkedHashMap()
    val source = Source.fromFile(file)
    var code: String = ""
    source.getLines.foreach(f => regex.findAllIn(f).matchData.foreach(p => {
      nameMap += (p.group(3).toInt -> p.group(2))
      code += p.group(4) + "\n"
    }))
    source.close()
    val infoMap = parseAST(code)
    if (infoMap == null) return null
    null
  }

  private def parseAST(code: String): mutable.LinkedHashMap[Int, (String, List[String])] = {
    val compilationUnit = JavaParser.parse(code)
    if (compilationUnit == null) return null
    val logs = new ListBuffer[String]
    compilationUnit.accept(new LogCollector, logs)
    if (logs.isEmpty) return null
    logs.foreach(println)
    null
  }

  class LogCollector extends VoidVisitorAdapter[ListBuffer[String]] {
    private val patterns: List[String] = {
      val source = Source.fromResource("pattern")
      val list = source.getLines.toList
      source.close()
      list
    }

    override def visit(n: ExpressionStmt, arg: ListBuffer[String]): Unit = {
      super.visit(n, arg)
      if (patterns.par.map(_.r.findFirstIn(n.toString)).exists(_.isDefined)) arg += n.toString
    }
  }

}
