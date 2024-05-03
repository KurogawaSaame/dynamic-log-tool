package multitier_log_agent.log_agent.transform.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;
import multitier_log_agent.log_agent.PatternUtil;
import multitier_log_agent.log_agent.transform.PointcutUtil;
import multitier_log_agent.log_agent.transform.TransformUtil;
import multitier_log_agent.log_agent.transform.TransformationRuleOrder;
import multitier_log_agent.log_agent.transform.instrumenter.CallInstrumenter;

import org.apache.commons.configuration.HierarchicalConfiguration;

public class ThreadCallPointcutRule extends MethodPointcutRule {

    public static final String Name = "ThreadCallPointcutRule";

    protected final List<Pattern> includeCalls = new ArrayList<Pattern>();

    public ThreadCallPointcutRule() {
        super(Name, TransformationRuleOrder.MethodCallPointcut.ordinal());
    }

    public ThreadCallPointcutRule(String name, int ordinal){
        super(name, ordinal);
    }

    public ThreadCallPointcutRule(HierarchicalConfiguration config) {
        this();
        logger.info("\tConfigure rule: " + getName());

        boolean traceCatch = config.getBoolean("trace-catch", false);
        logger.info("\t\tSet trace catch: " + traceCatch);
        setTraceCatch(traceCatch);

        boolean traceConstructor = config.getBoolean("trace-constructor", false);
        logger.info("\t\tSet trace constructor: " + traceConstructor);
        setTraceConstructor(traceConstructor);

        boolean traceParams = config.getBoolean("trace-params", false);
        logger.info("\t\tSet trace params: " + traceParams);
        setTraceParams(traceParams);

        for (HierarchicalConfiguration include : config.configurationsAt("include")) {
            String val = include.getString("");
            logger.info("\t\tAdd include: " + val);
            addInclude(val);
        }

        for (HierarchicalConfiguration include : config.configurationsAt("call-pattern")) {
            String val = include.getString("");
            logger.info("\t\tAdd include call: " + val);
            addIncludeCall(val);
        }

        for (HierarchicalConfiguration region : config.configurationsAt("region")) {
            addRegion(region.getString(""));
        }
    }

    public List<Pattern> getIncludeCalls() {
        return includeCalls;
    }

    public void addIncludeCall(String glob) {
        includeCalls.add(PatternUtil.glob2regex(glob));
    }

    public void addIncludeCall(Pattern pattern) {
        includeCalls.add(pattern);
    }

    @Override
    public boolean transform(CtClass cc) throws NotFoundException,
            CannotCompileException {
        boolean result = false;

        if (transform(cc.getDeclaredMethods())) {
            result = true;
        }
        if (isTraceConstructor() && transform(cc.getDeclaredConstructors())) {
            result = true;
        }
        return result;
    }

    protected boolean transform(CtBehavior[] declaredMethods) throws CannotCompileException, NotFoundException {
        boolean result = false;
        for (CtBehavior implMethod : declaredMethods) {
            if (transform(implMethod)) {
                result = true;
            }
        }
        return result;
    }

    protected boolean transform(CtBehavior m) throws CannotCompileException {
        if (TransformUtil.isSystemExitProxy(m)) {
            // we won't instrument our 'custom added methods'
            return false;
        }

        if (!PointcutUtil.isAbstract(m) && !m.isEmpty()
                && PointcutUtil.patternsAccept(include, m.getLongName())) {
            //System.out.println(1+m.getLongName());
            m.instrument(new CallInstrumenter(m, this));
            //System.out.println(2+m.getLongName());
            return true;
        }
        return false;
    }
}
