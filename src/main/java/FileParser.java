import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Author: stk
 * Date: 18/4/2
 */
public class FileParser {
    public static List<String> parseFile(String path) {
        CompilationUnit cu = null;
        try {
            cu = JavaParser.parse(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (cu == null) return null;
        List<String> logs = new ArrayList<>();
        cu.accept(new LogCollector(), logs);
        if (logs.size() == 0) return null;
        System.out.println("Parse " + path);
        String delimiter = IntStream.range(0, path.length()).mapToObj(value -> "=").collect(Collectors.joining(""));
        logs.add(0, delimiter);
        logs.add(1, path);
        logs.add(2, delimiter);
        return logs;
    }

    private static class LogCollector extends VoidVisitorAdapter<List<String>> {
        private static final List<String> patterns = Loader.loadPatterns();

        @Override
        public void visit(ExpressionStmt n, List<String> arg) {
            super.visit(n, arg);
            if (patterns.parallelStream().anyMatch(s -> Pattern.compile(s).matcher(n.toString()).find()))
                arg.add(n.toString());
        }
    }
}
