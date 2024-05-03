package multitier_log_agent.log_agent.transform.rule;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import javassist.*;
import multitier_log_agent.log_agent.PatternUtil;
import multitier_log_agent.log_agent.client.LogClientCodegen;
import multitier_log_agent.log_agent.classlist.ClazzGainner;
import multitier_log_agent.log_agent.query.IClassQuery;
import multitier_log_agent.log_agent.query.IMethodQuery;
import multitier_log_agent.log_agent.transform.AbstractTransformationRule;
import multitier_log_agent.log_agent.transform.DetectConstructorCall;
import multitier_log_agent.log_agent.transform.PointcutUtil;
import multitier_log_agent.log_agent.transform.TransformUtil;
import multitier_log_agent.log_agent.transform.TransformationRuleOrder;
import multitier_log_agent.log_agent.transform.instrumenter.CatchInstrumenter;
import multitier_log_agent.log_shared.model.JoinpointInfo.EventType;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;
import org.apache.commons.configuration.HierarchicalConfiguration;

public class MethodPointcutRule extends AbstractTransformationRule {

    public static final String Name = "MethodPointcutRule";

    private static final DetectConstructorCall detectConstructorCall = new DetectConstructorCall();

    protected final List<Pattern> include = new ArrayList<Pattern>();

    private String mine_pattern; //决定日志的挖掘模式
    private boolean traceCatch;
    private boolean traceConstructor;
    private boolean traceParams;  //同时return也用这个参数保证

    private static Set<String> instrumented_methods=new HashSet<>();

    public MethodPointcutRule() {
        super(Name, TransformationRuleOrder.MethodPointcut.ordinal());
    }

    public MethodPointcutRule(HierarchicalConfiguration config) {
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

        String mine_pattern = config.getString("mine_pattern");
        logger.info("\t\tSet drug model: " + traceParams);
        setMine_pattern(mine_pattern);

        for (HierarchicalConfiguration include : config.configurationsAt("include")) {
            String val = include.getString("");
            logger.info("\t\tAdd include: " + val);
            addInclude(val);
        }

        for (HierarchicalConfiguration region : config.configurationsAt("region")) {
            addRegion(region.getString(""));
        }
    }

    protected MethodPointcutRule(String name, int ordinal) {
        super(name, ordinal);
    }

    @Override
    public boolean isClassIncluded(IClassQuery query) {
        String fullDotName = query.getFullDotName();
        return PointcutUtil.patternsAccept(include, fullDotName);
    }

    @Override
    public boolean isMethodIncluded(IMethodQuery query) {
        String fullDotName = query.getFullDotName();
        return PointcutUtil.patternsAccept(include, fullDotName);
    }

    public boolean isTraceCatch() {
        return traceCatch;
    }

    public void setTraceCatch(boolean traceCatch) {
        this.traceCatch = traceCatch;
    }

    public boolean isTraceParams() {
        return traceParams;
    }

    public void setTraceConstructor(boolean traceConstructor) {
        this.traceConstructor = traceConstructor;
    }

    public boolean isTraceConstructor() {
        return traceConstructor;
    }

    public void setTraceParams(boolean traceParams) {
        this.traceParams = traceParams;
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

    public void setMine_pattern(String mine_pattern){
        this.mine_pattern=mine_pattern;
    }
    public String getMine_pattern(){
        return this.mine_pattern;
    }

    @Override
    public boolean transform(CtClass cc) throws NotFoundException, CannotCompileException {
        boolean result = false;
        for (CtMethod m : cc.getMethods()) {
            try {
                socketTransform(m);
            } catch (Exception e) {

            }
        }
        Map<String,Method> methodMap=getBcelMethods(cc);
        if (FUPtransform(cc.getMethods(), cc,methodMap)) {
            result = true;
        }
        else if (isTraceConstructor() && FUPtransform(cc.getDeclaredConstructors(), cc,methodMap)) {
            result = true;
        }
        try {
            cc.writeFile("./"+cc.getSimpleName()+".class");
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*if (PointcutUtil.patternsAccept(include, cc.getName())){
            String class_name=cc.getName();
            if(!ClazzGainner.savedClasses.contains(class_name)){
                ClazzGainner.savedClasses.add(class_name);
                ClazzGainner.getMoreClassesByName(class_name,include);
            }
        }*/
        return result;
    }

    protected boolean transform(CtBehavior[] declaredMethods, CtClass cc) throws CannotCompileException, NotFoundException {
        boolean result = false;
        for (CtBehavior implMethod : declaredMethods) {
            if (transform(implMethod, cc)) {
                result = true;
            }
        }
        return result;
    }

    //普通方法插装
    protected boolean transform(CtBehavior m, CtClass cc) throws CannotCompileException, NotFoundException {
        if (TransformUtil.isSystemExitProxy(m)) {
            // we won't instrument our 'custom added methods'
            return false;
        }

        if (!PointcutUtil.isAbstract(m) && PointcutUtil.patternsAccept(include, m.getLongName())){
            logger.debug("\t普通方法插装: '" + m.getLongName() + "'");
            String targetRef = "this";
            if (Modifier.isStatic(m.getModifiers())) {
                targetRef = null;
            }

            String recordEntry, recordExit;
            if (m instanceof CtConstructor) {//是构造方法
                recordEntry = LogClientCodegen.recordJoinpoint(
                        cc, m, targetRef, EventType.CALL_NEW, getRegionJavaString(), mine_pattern,traceParams, false);
                recordExit = LogClientCodegen.recordJoinpoint(
                        cc, m, targetRef, EventType.RETURN_NEW, getRegionJavaString(),mine_pattern, traceParams, false);
            } else { //普通方法
                recordEntry = LogClientCodegen.recordJoinpoint(
                        cc, m, targetRef, EventType.CALL, getRegionJavaString(), mine_pattern, traceParams, traceParams);
                recordExit = LogClientCodegen.recordJoinpoint(
                        cc, m, targetRef, EventType.RETURN, getRegionJavaString(), mine_pattern, traceParams, traceParams);
           }
            String recordThrow = LogClientCodegen.recordExceptionThrowJoinpoint(
                    m, targetRef, EventType.THROW, getRegionJavaString());

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
                    System.out.println("！！！！！！！！！！！！！transform插入头成功！！！！！！！！！！！！！！！");
                }else{
                    System.out.println(m.getLongName() + "here dont instrument CtConstructor before!!!");
                }

                if (TransformUtil.getThrowableType() != null) {
                    // we use a separate catch for exceptions
                    m.insertAfter(recordExit, false);
                    System.out.println("！！！！！！！！！！！！！transform插入尾成功！！！！！！！！！！！！！！！");
                    m.addCatch(recordThrow, TransformUtil.getThrowableType());
                } else {
                    System.out.println("！！！！！！！！！！！！！transform插入尾成功！！！！！！！！！！！！！！！");
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

    protected boolean transform(CtConstructor[] declaredMethods, CtClass cc) throws CannotCompileException, NotFoundException {
        boolean result = false;
        for (CtConstructor implMethod : declaredMethods) {
            if (transform(implMethod, cc)) {
                result = true;
            }
        }
        return result;
    }

    protected boolean transform(CtConstructor m, CtClass cc) throws CannotCompileException, NotFoundException {
        if (TransformUtil.isSystemExitProxy(m)) {
            // we won't instrument our 'custom added methods'
            return false;
        }
        if (PointcutUtil.patternsAccept(include, m.getLongName())) {

            logger.debug("\t构造方法插装: '" + m.getLongName() + "'");

            String targetRef = "this";

            String recordEntry, recordExit;
            recordEntry = LogClientCodegen.recordJoinpoint(//todo:最后一个参数被修改过，看需要可回退
                        cc, m, targetRef, EventType.CALL_NEW, getRegionJavaString(), mine_pattern, traceParams, false);

            recordExit = LogClientCodegen.recordJoinpoint(
                        cc, m, targetRef, EventType.RETURN_NEW, getRegionJavaString(), mine_pattern, traceParams, false);

            String recordThrow = LogClientCodegen.recordExceptionThrowJoinpoint(
                    m, targetRef, EventType.THROW, getRegionJavaString());

            //todo:好好处理研究一下
//            String recordEntry2 = LogClientCodegen.recordJoinpoint(
//                    cc, m, targetRef, EventType.RETURN_NEW, getRegionJavaString(), mine_pattern, traceParams, false);
            String recordEntry2 = LogClientCodegen.recordJoinpoint2(m, cc, EventType.CALL_NEW, getRegionJavaString(), mine_pattern);

            if (m.isEmpty()) {
                // no body -> insert records as new body
                m.setBody("{" + recordEntry + " \n " + recordExit + "}");
            } else {
                // existing body -> wrap records around it
                // Handle catch clauses if desired

                /**
                 * 研究行为用得上
                 */
                if (isTraceCatch()) {
                    m.instrument(new CatchInstrumenter(m, this));
                }

                // Handle normal call, and normal plus thrown exception return
                boolean insertBefore = true;
                detectConstructorCall.resetDetection();
                m.instrument(detectConstructorCall);
                insertBefore = !detectConstructorCall.detectedConstructorCall();

                if(insertBefore) {
                    m.insertBefore(recordEntry);
                }else{
                    m.insertBefore(recordEntry2);
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
            System.out.println("now instrument gouzao OK" + m.getLongName());
            return true;
        }
        return false;
    }

    protected void socketTransform(CtMethod m)throws CannotCompileException, NotFoundException{
        m.insertBefore(LogClientCodegen.recordCommJoinpointSocket(
                m, EventType.CALL_SOCKET, "socket", getRegionJavaString()));
        m.insertAfter(LogClientCodegen.recordCommJoinpointSocket(
                m, EventType.RETURN_SOCKET, "socket", getRegionJavaString()));
    }

    protected boolean FUPtransform(CtBehavior[] declaredMethods, CtClass cc,Map<String,Method> methodMap) throws CannotCompileException, NotFoundException {
        boolean result = false;

        for (CtBehavior implMethod : declaredMethods) {
            if (FUPtransform(implMethod, cc,methodMap)) {
                result = true;
            }
        }
        return result;
    }

    //普通方法插装
    protected boolean FUPtransform(CtBehavior m, CtClass cc,Map<String,Method> methodMap) throws CannotCompileException, NotFoundException {
        if (TransformUtil.isSystemExitProxy(m)) {
            // we won't instrument our 'custom added methods'
            return false;
        }
        if (!PointcutUtil.isAbstract(m) && PointcutUtil.patternsAccept(include, m.getLongName())
                &&!instrumented_methods.contains(m.getLongName())) {
            instrumented_methods.add(m.getLongName());
            logger.debug("\t普通方法插装: '" + m.getLongName() + "'");
            String targetRef = "this";
            if (Modifier.isStatic(m.getModifiers())) {
                targetRef = null;
            }

            String recordEntry, recordExit;
            if (m instanceof CtConstructor) {//是构造方法
                recordEntry = LogClientCodegen.recordJoinpointFUP(
                        cc, m, targetRef, EventType.CALL_NEW, getRegionJavaString(), mine_pattern,traceParams, false,methodMap,include);
                recordExit = LogClientCodegen.recordJoinpointFUP(
                        cc, m, targetRef, EventType.RETURN_NEW, getRegionJavaString(),mine_pattern, traceParams, false,methodMap,include);
            } else { //普通方法
                recordEntry = LogClientCodegen.recordJoinpointFUP(
                        cc, m, targetRef, EventType.CALL, getRegionJavaString(), mine_pattern, traceParams, traceParams,methodMap,include);
                recordExit = LogClientCodegen.recordJoinpointFUP(
                        cc, m, targetRef, EventType.RETURN, getRegionJavaString(), mine_pattern, traceParams, traceParams,methodMap,include);
            }
            String recordThrow = LogClientCodegen.recordExceptionThrowJoinpoint(
                    m, targetRef, EventType.THROW, getRegionJavaString());

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
                }else{
                    System.out.println(m.getLongName() + "here dont instrument CtConstructor before!!!");
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

    protected boolean FUPtransform(CtConstructor[] declaredMethods, CtClass cc,Map<String,Method> methodMap) throws CannotCompileException, NotFoundException {
        boolean result = false;
        for (CtConstructor implMethod : declaredMethods) {
            if (FUPtransform(implMethod, cc,methodMap)) {
                result = true;
            }
        }
        return result;
    }

    protected boolean FUPtransform(CtConstructor m, CtClass cc,Map<String,Method> methodMap)
            throws CannotCompileException, NotFoundException {
        if (TransformUtil.isSystemExitProxy(m)) {
            // we won't instrument our 'custom added methods'
            return false;
        }
        if (PointcutUtil.patternsAccept(include, m.getLongName())
                &&!instrumented_methods.contains(m.getLongName())) {
            instrumented_methods.add(m.getLongName());

            logger.debug("\t构造方法插装: '" + m.getLongName() + "'");

            String targetRef = "this";

            String recordEntry, recordExit;
            recordEntry = LogClientCodegen.recordJoinpointFUP(//todo:最后一个参数被修改过，看需要可回退
                    cc, m, targetRef, EventType.CALL_NEW, getRegionJavaString(), mine_pattern, traceParams, false,methodMap,include);

            recordExit = LogClientCodegen.recordJoinpointFUP(
                    cc, m, targetRef, EventType.RETURN_NEW, getRegionJavaString(), mine_pattern, traceParams, false,methodMap,include);

            String recordThrow = LogClientCodegen.recordExceptionThrowJoinpoint(
                    m, targetRef, EventType.THROW, getRegionJavaString());

            //todo:好好处理研究一下
//            String recordEntry2 = LogClientCodegen.recordJoinpoint(
//                    cc, m, targetRef, EventType.RETURN_NEW, getRegionJavaString(), mine_pattern, traceParams, false);
            String recordEntry2 = LogClientCodegen.recordJoinpointFUP2(m, cc, EventType.CALL_NEW, getRegionJavaString(), mine_pattern,methodMap,include);

            if (m.isEmpty()) {
                // no body -> insert records as new body
                m.setBody("{" + recordEntry + " \n " + recordExit + "}");
            } else {
                // existing body -> wrap records around it
                // Handle catch clauses if desired

                /**
                 * 研究行为用得上
                 */
                if (isTraceCatch()) {
                    m.instrument(new CatchInstrumenter(m, this));
                }

                // Handle normal call, and normal plus thrown exception return
                boolean insertBefore = true;
                detectConstructorCall.resetDetection();
                m.instrument(detectConstructorCall);
                insertBefore = !detectConstructorCall.detectedConstructorCall();

                if(insertBefore) {
                    m.insertBefore(recordEntry);
                }else{
                    String name = m.getLongName();
                    m.insertBefore(recordEntry2);
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
            System.out.println("now instrument gouzao OK" + m.getLongName());
            return true;
        }
        return false;
    }

    protected Map<String,Method> getBcelMethods(CtClass cc){
        Map<String,Method> map=new HashMap<>();
        try {
            JavaClass clazz = Repository.lookupClass(cc.getName());
            Method[] ms=clazz.getMethods();
            for(Method m:ms){
                StringBuilder m_name=new StringBuilder();
                if(m.getName().equals("<init>"))
                    m_name.append(cc.getSimpleName()).append("(");
                else
                    m_name.append(m.getName()).append("(");
                String sep="";
                for(Type p:m.getArgumentTypes()){
                    m_name.append(sep);
                    m_name.append(p.toString());
                    sep=",";
                }
                m_name.append(")");
                map.put(m_name.toString(),m);
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Can't find Class "+cc.getName());
        }
        return map;
    }

}
