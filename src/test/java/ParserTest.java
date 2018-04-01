import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Author: stk
 * Date: 18/4/1
 */
public class ParserTest {
    private Parser parser;
    @Before
    public void init() {
        parser = new Parser("", "C:\\Users\\stk\\Downloads\\test-result.txt");
    }

    @Test
    public void parseFile() {
        String file = "C:\\Users\\stk\\Documents\\Docs\\Papers\\EASE2018Log\\Data\\elasticsearch\\test\\framework\\src\\main\\java\\org\\elasticsearch\\transport\\MockTcpTransport.java";
        try {
            Method parseFile = parser.getClass().getDeclaredMethod("parseFile", Path.class);
            parseFile.setAccessible(true);
            parseFile.invoke(parser, Paths.get(file));
            parser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
