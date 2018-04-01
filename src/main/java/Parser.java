import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Author: stk
 * Date: 18/4/1
 */
public class Parser {
    private static final List<String> patterns = PatternLoader.loadPatterns();
    private String projectPath;
    private BufferedWriter writer;

    public Parser(String projectPath, String outputPath) {
        this.projectPath = projectPath;
        if (new File(outputPath).delete()) System.out.println("Delete existing result file.");
        try {
            writer = new BufferedWriter(new FileWriter(outputPath, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Parser parser = new Parser("C:\\Users\\stk\\Documents\\Docs\\Papers\\EASE2018Log\\Data\\elasticsearch", "C:\\Users\\stk\\Downloads\\result.txt");
        parser.parseProject();
        parser.close();
    }

    public void parseProject() {
        try {
            Files.walk(Paths.get(projectPath)).filter(i -> i.toString().endsWith(".java")).forEach(this::parseFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseFile(Path path) {
        CompilationUnit cu = null;
        try {
            cu = JavaParser.parse(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (cu == null) return;
        List<String> logs = new ArrayList<>();
        cu.accept(new LogCollector(), logs);
        if (logs.size() == 0) return;
        String file = path.toString();
        System.out.println("Parse " + file);
        try {
            writer.write(IntStream.range(0, file.length()).mapToObj(i -> "=").collect(Collectors.joining("")));
            writer.write("\n" + file + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        logs.forEach(i -> {
            try {
                writer.write(i + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void close() {
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class LogCollector extends VoidVisitorAdapter<List<String>> {
        @Override
        public void visit(ExpressionStmt n, List<String> arg) {
            super.visit(n, arg);
            if (patterns.parallelStream().anyMatch(i -> Pattern.compile(i).matcher(n.toString()).find()))
                arg.add(n.toString());
        }
    }
}
