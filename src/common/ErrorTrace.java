package common;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Arbor vlinyq@gmail.com
 * @version 2017/3/9
 */
public class ErrorTrace {
    public static String getTrace(Throwable t) {
        StringWriter stringWriter= new StringWriter();
        PrintWriter writer= new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        StringBuffer buffer= stringWriter.getBuffer();
        return buffer.toString();
    }
}
