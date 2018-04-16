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
  private val regex = "".r

  def parseFile(file: File): List[String] = {
    println("Parse " + file)
    regex.findAllIn().matchData foreach {
      m => println(m.group(1))
    }
  }

  def parseAST(code: String): Unit = {
    val compilationUnit = JavaParser.parse(code)
    if (compilationUnit == null) return null
    val logs = new ListBuffer[String]
    compilationUnit.accept(new LogCollector, logs)
    if (logs.isEmpty) return null
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
