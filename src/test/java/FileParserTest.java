import org.junit.Test;

import java.util.List;

/**
 * Author: stk
 * Date: 18/4/2
 */
public class FileParserTest {
    @Test
    public void parseFile() {
        String file = "C:\\Users\\stk\\Documents\\Docs\\Papers\\EASE2018Log\\Data\\elasticsearch\\test\\framework\\src\\main\\java\\org\\elasticsearch\\transport\\MockTcpTransport.java";
        List<String> result = FileParser.parseFile(file);
        assert result != null;
        result.forEach(System.out::println);
    }
}
