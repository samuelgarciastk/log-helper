import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Author: stk
 * Date: 18/4/1
 */
public class Parser {
    public static void main(String[] args) {
        String projectPath = "C:\\Users\\stk\\Documents\\Docs\\Papers\\EASE2018Log\\Data\\elasticsearch";
        String outputPath = "C:\\Users\\stk\\Downloads\\result.txt";
        Parser parser = new Parser();
        parser.generate(parser.parseProject(projectPath), outputPath);
    }

    public List<String> parseProject(String path) {
        try {
            return Files.walk(Paths.get(path))
                    .filter(p -> p.toString().endsWith(".java"))
                    .map(p -> FileParser.parseFile(p.toString()))
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException();
    }

    public void generate(List<String> logs, String path) {
        if (new File(path).delete()) System.out.println("Delete existing result file.");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            logs.forEach(s -> {
                try {
                    writer.write(s);
                    writer.write("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
