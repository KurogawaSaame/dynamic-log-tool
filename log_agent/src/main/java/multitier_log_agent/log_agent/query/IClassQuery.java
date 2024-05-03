package multitier_log_agent.log_agent.query;

import java.util.List;

public interface IClassQuery {

    public String getFullDotName();
    
    public List<String> getInterfaceFullDotNames();
    
    public List<IClassQuery> getInterfaces();
    
    public List<IMethodQuery> getMethods();
    
    public boolean equals(IClassQuery other);
}
