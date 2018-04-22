import com.github.javaparser.JavaParser
import com.github.javaparser.ast.stmt._
import com.github.javaparser.ast.visitor.VoidVisitorAdapter

import scala.collection.mutable

/**
  * Author: stk
  * Date: 18/4/22
  */
object ASTParser {
  def parseAST(code: String): mutable.LinkedHashMap[Int, mutable.ArrayBuffer[String]] = {
    val cu = JavaParser.parse(code)
    if (cu == null) return null
    val infoMap: mutable.LinkedHashMap[Int, mutable.ArrayBuffer[String]] = mutable.LinkedHashMap()
    cu.accept(new LogCollector, infoMap)
    if (infoMap.isEmpty) return null
    infoMap
  }

  class LogCollector extends VoidVisitorAdapter[mutable.LinkedHashMap[Int, mutable.ArrayBuffer[String]]] {
    override def visit(n: ExpressionStmt, arg: mutable.LinkedHashMap[Int, mutable.ArrayBuffer[String]]): Unit = {
      super.visit(n, arg)
      val exp = n.removeComment().asInstanceOf[ExpressionStmt]
      val patterns = LogPattern.patterns.par.filter(_.r.findFirstIn(exp.toString).isDefined)
      if (patterns.nonEmpty) {
        if (exp.toString.split("\n").length > 1) {
          exp.getChildNodes.forEach(f => f.accept(this, arg))
        } else {
          val matcher = patterns.head.r.findFirstMatchIn(exp.toString).get
          val level = matcher.group(1)
          val nodeType = exp.getParentNode.get.getParentNode.get.getMetaModel.getTypeName
          arg += (exp.getBegin.get.line -> mutable.ArrayBuffer(level, nodeType, exp.toString))
        }
      }
    }
  }

}
