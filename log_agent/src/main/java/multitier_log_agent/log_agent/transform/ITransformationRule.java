package multitier_log_agent.log_agent.transform;

import java.util.List;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import multitier_log_agent.log_agent.query.IClassQuery;
import multitier_log_agent.log_agent.query.IMethodQuery;

public interface ITransformationRule extends Comparable<ITransformationRule> {

    public String getName();

    public int getOrdinal();
    
    public boolean isClassIncluded(IClassQuery query);
    
    public boolean isMethodIncluded(IMethodQuery query);

    public List<String> getRegions();
    
    public String getRegionJavaString();
    
    // return true iff transformed class
    public boolean transform(CtClass cc) throws NotFoundException, CannotCompileException;
}
