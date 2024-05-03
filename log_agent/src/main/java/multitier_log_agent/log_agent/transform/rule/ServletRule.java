package multitier_log_agent.log_agent.transform.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import multitier_log_agent.log_agent.PatternUtil;
import multitier_log_agent.log_agent.client.LogClientCodegen;
import multitier_log_agent.log_agent.query.IClassQuery;
import multitier_log_agent.log_agent.query.IMethodQuery;
import multitier_log_agent.log_agent.transform.AbstractTransformationRule;
import multitier_log_agent.log_agent.transform.PointcutUtil;
import multitier_log_agent.log_agent.transform.TransformationRuleOrder;
import multitier_log_agent.log_shared.model.JoinpointInfo.EventType;

import org.apache.commons.configuration.HierarchicalConfiguration;

public class ServletRule extends AbstractTransformationRule {

    public static final String Name = "ServletRule";

    protected final List<Pattern> include = new ArrayList<Pattern>();

    private static final String TARGET_INT = "javax.servlet.Servlet";

    private static final String ENTRY_NAME = "service";
    private static final String ENTRY_DESC = "(Ljavax/servlet/ServletRequest;Ljavax/servlet/ServletResponse;)V";
    private static final String Entry_Method_Name =
            ENTRY_NAME + "(javax.servlet.ServletRequest,javax.servlet.ServletResponse)";
    
    private static final Pattern Entry_Method_Pattern = 
            PatternUtil.glob2regex("*." + Entry_Method_Name);
    
    public ServletRule() {
        super(Name, TransformationRuleOrder.Servlet.ordinal());
    }

    public ServletRule(HierarchicalConfiguration config) {
        this();
        logger.info("\tConfigure rule: " + getName());

        for (HierarchicalConfiguration include : config.configurationsAt("include")) {
            String val = include.getString("");
            logger.info("\t\tAdd include: " + val);
            addInclude(val);
        }
        
        for (HierarchicalConfiguration region : config.configurationsAt("region")) {
            addRegion(region.getString(""));
        }
    }
    
    public List<Pattern> getInclude() {
        return include;
    }

    public void addInclude(String glob) {
        include.add(PatternUtil.glob2regex(glob));
    }

    public void addInclude(Pattern pattern) {
        include.add(pattern);
    }

    @Override
    public boolean isClassIncluded(IClassQuery query) {
        String fullDotName = query.getFullDotName();
        List<String> interfaces = query.getInterfaceFullDotNames();
        return PointcutUtil.patternsAccept(include, fullDotName)
                && interfaces.contains(TARGET_INT);
    }

    @Override
    public boolean isMethodIncluded(IMethodQuery query) {
        IClassQuery qClass = query.getClassQuery();
        String fullDotClassName = qClass.getFullDotName();
        List<String> interfaces = qClass.getInterfaceFullDotNames();
        String fullDotMethodName = query.getFullDotName();
        return PointcutUtil.patternsAccept(include, fullDotClassName)
                && interfaces.contains(TARGET_INT)
                && Entry_Method_Pattern.matcher(fullDotMethodName).matches();
    }
    
    @Override
    public boolean transform(CtClass cc) throws NotFoundException,
            CannotCompileException {
        
        if (PointcutUtil.hasInterface(cc, TARGET_INT)
                && PointcutUtil.patternsAccept(include, cc.getName())) {
            // find target method
            CtMethod m = cc.getMethod(ENTRY_NAME, ENTRY_DESC);

            // check if method is locally implemented / has a method body
            if (PointcutUtil.hasBody(m)) {
                m.insertBefore(LogClientCodegen.recordCommJoinpointServlet(
                        m, EventType.CALL_SERVLET, "$1", getRegionJavaString()));
                m.insertAfter(LogClientCodegen.recordCommJoinpointServlet(
                        m, EventType.RETURN_SERVLET, "$1", getRegionJavaString()));
                return true;
            }
        }
        
        return false;
    }
    
}
