package multitier_log_agent.log_agent;

import java.util.regex.Pattern;

public class PatternUtil {

    public static Pattern glob2regex(String pattern) {
	String regex = "\\Q" + pattern.replace("*", "\\E.*?\\Q") + "\\E";
	return Pattern.compile(regex);
    }
    
    public static String regex2glob(Pattern pattern) {
        String regex = pattern.pattern();
        if (regex.startsWith("\\Q") && regex.endsWith("\\E")) {
            regex = regex.replace("\\E.*?\\Q", "*");
            regex = regex.substring(2, regex.length() - 2);
        }
        return regex;
    }
}
