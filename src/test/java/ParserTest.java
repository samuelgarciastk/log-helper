import org.junit.Test;

import java.util.List;

/**
 * Author: stk
 * Date: 18/4/3
 */
public class ParserTest {
    @Test
    public void parseProject() {
        Parser parser = new Parser();
        List<String> result = parser.parseProject("C:\\Users\\stk\\Downloads\\transport");
        result.forEach(System.out::println);
    }
}
