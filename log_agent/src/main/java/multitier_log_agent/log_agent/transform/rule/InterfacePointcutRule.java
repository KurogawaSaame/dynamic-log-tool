package multitier_log_agent.log_agent.transform.rule;

import java.util.List;

import javassist.*;
import multitier_log_agent.log_agent.client.LogClientCodegen;
import multitier_log_agent.log_agent.transform.DetectConstructorCall;
import multitier_log_agent.log_agent.transform.TransformUtil;
import multitier_log_agent.log_agent.transform.instrumenter.CatchInstrumenter;
import multitier_log_agent.log_shared.model.JoinpointInfo;
import org.apache.commons.configuration.HierarchicalConfiguration;

import multitier_log_agent.log_agent.query.IClassQuery;
import multitier_log_agent.log_agent.query.IMethodQuery;
import multitier_log_agent.log_agent.transform.PointcutUtil;
import multitier_log_agent.log_agent.transform.TransformationRuleOrder;

public class InterfacePointcutRule extends MethodPointcutRule {

    public static final String Name = "InterfacePointcutRule";

    private static final DetectConstructorCall detectConstructorCall = new DetectConstructorCall();

    public InterfacePointcutRule() {
        super(Name, TransformationRuleOrder.InterfacePointcut.ordinal());
    }

    public InterfacePointcutRule(HierarchicalConfiguration config) {
        this();
        logger.info("\tConfigure rule: " + getName());

        boolean traceCatch = config.getBoolean("trace-catch", false);
        logger.info("\t\tSet trace catch: " + traceCatch);
        setTraceCatch(traceCatch);
        
        for (HierarchicalConfiguration include : config.configurationsAt("include")) {
            String val = include.getString("");
            logger.info("\t\tAdd include: " + val);
            addInclude(val);
        }
        
        for (HierarchicalConfiguration region : config.configurationsAt("region")) {
            addRegion(region.getString(""));
        }
    }
    
    @Override
    public boolean isClassIncluded(IClassQuery query) {
        List<String> interfaces = query.getInterfaceFullDotNames();
        for (String intName : interfaces) {
            if (PointcutUtil.patternsAccept(include, intName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMethodIncluded(IMethodQuery query) {
        for (IClassQuery inter : query.getClassQuery().getInterfaces()) {
            if (PointcutUtil.patternsAccept(include, inter.getFullDotName())) {
                for (IMethodQuery m : inter.getMethods()) {
                    if (query.equals(m)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean transform(CtClass cc) throws NotFoundException,
            CannotCompileException {
        boolean result = false;

        CtClass[] interfaces = cc.getInterfaces();
        for (CtClass ctInterface : interfaces) {
            if (PointcutUtil.patternsAccept(include, ctInterface.getName())) {
                transform(cc, ctInterface);
            }
        }

        return result;
    }

    protected void transform(CtClass cc, CtClass ctInterface) throws CannotCompileException {
        for (CtMethod intMethod : ctInterface.getMethods()) {
            if (Modifier.isAbstract(intMethod.getModifiers())) {
                try {
                    logger.debug("Lookup method '"
                            + intMethod.getLongName() + "'");
                    CtMethod implMethod = cc.getMethod(intMethod.getName(),
                            intMethod.getSignature());

                    transform(implMethod, ctInterface, cc.getName());

                } catch (NotFoundException e) {
                    logger.warn(
                            "Could not find method '" + intMethod.getLongName()
                                    + "'", e);
                }
            }
        }
    }

    //入参，实现类中的此方法，含有此方法的实现类的接口，实现类的类名
    protected boolean transform(CtBehavior m, CtClass ctInterface, String className) throws CannotCompileException, NotFoundException {
        if (TransformUtil.isSystemExitProxy(m)) {
            // we won't instrument our 'custom added methods'
            return false;
        }

        if (!PointcutUtil.isAbstract(m)
                && PointcutUtil.patternsAccept(include, m.getLongName())) {

            logger.debug("\tInstrument on method '" + m.getLongName() + "'");

            String targetRef = "this";
            if (Modifier.isStatic(m.getModifiers())) {
                targetRef = null;
            }

            String recordEntry, recordExit;
            recordEntry = LogClientCodegen.recordInterface(
                    ctInterface, m, className, targetRef, JoinpointInfo.EventType.CALL_INTERFACE, getRegionJavaString(), false, false);
            recordExit = LogClientCodegen.recordInterface(
                    ctInterface, m, className, targetRef, JoinpointInfo.EventType.RETURN_INTERFACE, getRegionJavaString(), false, false);
            String recordThrow = LogClientCodegen.recordExceptionThrowJoinpoint(
                    m, targetRef, JoinpointInfo.EventType.THROW, getRegionJavaString());

            if (m.isEmpty()) {
                // no body -> insert records as new body
                m.setBody("{" + recordEntry + " \n " + recordExit + "}");
            } else {
                // existing body -> wrap records around it
                // Handle catch clauses if desired
                if (isTraceCatch()) {
                    m.instrument(new CatchInstrumenter(m, this));
                }

                // Handle normal call, and normal plus thrown exception return
                boolean insertBefore = true;
                if (m instanceof CtConstructor) {
                    detectConstructorCall.resetDetection();
                    m.instrument(detectConstructorCall);
                    insertBefore = !detectConstructorCall.detectedConstructorCall();
                }
                if(insertBefore) {
                    m.insertBefore(recordEntry);
                }

                if (TransformUtil.getThrowableType() != null) {
                    // we use a separate catch for exceptions
                    m.insertAfter(recordExit, false);
                    m.addCatch(recordThrow, TransformUtil.getThrowableType());
                } else {
                    // we use both normal and exception return the same as a fallback
                    m.insertAfter(recordExit, true);
                }

                // Handle system exit
                TransformUtil.addSystemExitProxy(m, recordExit);
            }
            return true;
        }
        return false;
    }

}
