package multitier_log_agent.log_agent.query;

public interface IMethodQuery {

    public String getFullDotName();

    public IClassQuery getClassQuery();
    
    public boolean equals(IMethodQuery other);
}
