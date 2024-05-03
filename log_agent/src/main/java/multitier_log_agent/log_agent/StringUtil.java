package multitier_log_agent.log_agent;

public class StringUtil {

    public static String join(String[] input, String sepToken) {
	StringBuilder builder = new StringBuilder();
	String sep = "";
	for (String val : input) {
	    builder.append(sep);
	    builder.append(val);
	    sep = sepToken;
	}
	return builder.toString();
    }

}
