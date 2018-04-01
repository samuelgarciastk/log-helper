import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: stk
 * Date: 18/4/1
 */
public class PatternLoader {
    public static List<String> loadPatterns() {
        List<String> patterns = new ArrayList<>();
        try (InputStreamReader in = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("pattern"), "UTF-8");
             BufferedReader reader = new BufferedReader(in)) {
            String line;
            while ((line = reader.readLine()) != null) patterns.add(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return patterns;
    }
}
