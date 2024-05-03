package multitier_log_agent.log_agent.transform;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractTransformationRule 
    implements ITransformationRule {

    public static Logger logger = LogManager.getLogger(AbstractTransformationRule.class);
    
    private final String name;
    
    private final List<String> regions = new ArrayList<String>();

    private int ordinal;

    private String regionJavaString;

    public AbstractTransformationRule(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getRegions() {
        return regions;
    }

    @Override
    public String getRegionJavaString() {
        if (regionJavaString == null) {
            if (regions == null || regions.isEmpty()) {
                regionJavaString = "new java.lang.String[0]";
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append("new java.lang.String[] {");

                String sep = "";
                for (String region : regions) {
                    builder.append(sep);
                    builder.append("\"");
                    builder.append(region.replaceAll("[\"]", "\\\""));
                    builder.append("\"");
                    sep = ", ";
                }

                builder.append("}");

                regionJavaString = builder.toString();
            }
        }
        return regionJavaString;
    }

    public void addRegion(String region) {
        regions.add(region);
    }

    @Override
    public int compareTo(ITransformationRule o) {
        return Integer.compare(getOrdinal(), o.getOrdinal());
    }
}
