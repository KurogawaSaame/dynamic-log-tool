package multitier_log_agent.log_agent.client;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Test;

import java.util.HashSet;

public class TestgetObjectString {
    @Test
    public void printString(){
        String[] strings={"one","two","three"};
        String value=LogClientCodegen.getObjectString(strings,new HashSet<String>(),0);
        System.out.println(value);
    }
}
