import java.io.File

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.stmt.ExpressionStmt
import com.github.javaparser.ast.visitor.VoidVisitorAdapter

import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Author: stk
  * Date: 18/4/6
  */
object FileParser {
  def parseFile(file: File): List[String] = {
    val compilationUnit = JavaParser.parse(file)
    if (compilationUnit == null) return null
    val logs = new ListBuffer[String]
    compilationUnit.accept(new LogCollector, logs)
    if (logs.isEmpty) return null
    println("Parse " + file)
    logs.toList
  }

  class LogCollector extends VoidVisitorAdapter[ListBuffer[String]] {
    private val patterns: List[String] = Source.fromResource("pattern").getLines.toList

    override def visit(n: ExpressionStmt, arg: ListBuffer[String]): Unit = {
      super.visit(n, arg)
      if (patterns.par.map(_.r.findFirstIn(n.toString()) match {
        case Some(p) => true
        case None => false
      }).filter(_ == true).toList.nonEmpty) arg += n.toString
    }
  }
}
