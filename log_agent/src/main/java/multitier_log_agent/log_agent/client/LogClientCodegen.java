package multitier_log_agent.log_agent.client;

import javassist.*;
import javassist.expr.ConstructorCall;
import javassist.expr.Expr;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;
import multitier_log_agent.log_agent.classlist.ClassesNames;
import multitier_log_agent.log_agent.transform.PointcutUtil;
import multitier_log_agent.log_agent.util.MethodVisitor;
import multitier_log_agent.log_agent.util.TransformedFlag;
import multitier_log_agent.log_shared.model.*;
import multitier_log_agent.log_shared.model.JoinpointInfo.DataType;
import multitier_log_agent.log_shared.model.JoinpointInfo.EventType;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Pattern;

public class LogClientCodegen {
    public static boolean flag=false;

    public static final Logger logger = LogManager.getLogger(LogClientCodegen.class);

    private static final String datatypeTypeStr = JoinpointInfo.DataType.class.getName().replace('$', '.');

    public LogClientCodegen() {
    }

    public static String recordJoinpoint(CtClass cc, CtBehavior m, String targetRef, EventType eventType,
            String regionJavaString, String drug_pattern, boolean includeParams, boolean includeReturn) throws NotFoundException {
        StringBuilder result = new StringBuilder();
        result.append("{");
        result.append(ClassesNames.class.getName()).append(".printClassesInfo();");
        result.append(LogClient.class.getName());
        result.append(".recordJoinpoint(");
        switch (drug_pattern) {
            case "pure":
                createJoinpointObject_pure(cc, result, m, targetRef, eventType, includeParams, includeReturn);
                break;
            case "common":
                createJoinpointObject_common(cc, result, m, targetRef, eventType, includeParams, includeReturn);
                break;
            case "param":
                createJoinpointObject_param(cc, result, m, targetRef, eventType, includeParams, includeReturn);
                break;
        }
        result.append(",\"");
        result.append(drug_pattern);
        result.append("\",");
        result.append(regionJavaString);
        result.append(");");
        result.append("}");
        return result.toString();
    }

    public static String recordJoinpoint2(CtConstructor m, CtClass cc, EventType eventType,
                                          String regionJavaString, String drug_pattern) throws NotFoundException {
        StringBuilder result = new StringBuilder();
        result.append("{");
        result.append(LogClient.class.getName());
        result.append(".recordJoinpoint(");
        switch (drug_pattern) {
            case "pure":
                createJoinpointObject_pure_2(cc, result, m, "this", eventType, true, false);
                break;
            case "common":
                createJoinpointObject_common_2(cc, result, m, "this", eventType, true, false);
                break;
            case "param":
                createJoinpointObject_param_2(cc, result, m, "this", eventType, true, false);
                break;
        }
//        createJoinpointObject2(cc, result, m, "this", eventType, true, false);
        result.append(",\"");
        result.append(drug_pattern);
        result.append("\",");
        result.append(regionJavaString);
        result.append(");");
        result.append("}");
        return result.toString();
    }

    public static String recordJoinpointFUP(CtClass cc, CtBehavior m, String targetRef, EventType eventType,
                                            String regionJavaString, String drug_pattern, boolean includeParams, boolean includeReturn,
                                            Map<String, org.apache.bcel.classfile.Method> methodMap,List<Pattern> include) throws NotFoundException {
        StringBuilder result = new StringBuilder();
        result.append("{");;/*
        result.append("if(!multitier_log_agent.log_agent.util.TransformedFlag.flag){");
        result.append("multitier_log_agent.log_agent.util.TransformedFlag.flag=true;");*/
        result.append(ClassesNames.class.getName()).append(".printClassesInfo();");
        result.append(LogClient.class.getName());
        result.append(".recordJoinpoint(");
        switch (drug_pattern) {
            case "pure":
                createJoinpointObject_pure(cc, result, m, targetRef, eventType, includeParams, includeReturn);
                break;
            case "common":
                createJoinpointObject_common(cc, result, m, targetRef, eventType, includeParams, includeReturn);
                break;
            case "param":
                createJoinpointObject_param(cc, result, m, targetRef, eventType, includeParams, includeReturn);
                break;
            case "FUP":
                createJoinpointObject_FUP(cc, result, m, targetRef, eventType, includeParams, includeReturn,methodMap,include);
                break;
        }
        result.append(",\"");
        result.append(drug_pattern);
        result.append("\",");
        result.append(regionJavaString);
        result.append(");");/*
        result.append("}");
        result.append("else{multitier_log_agent.log_agent.util.TransformedFlag.flag=false;}");*/
        result.append("}");
        return result.toString();
    }
    public static String recordJoinpointFUP2(CtConstructor m, CtClass cc, EventType eventType,
                                             String regionJavaString, String drug_pattern,
                                             Map<String, org.apache.bcel.classfile.Method> methodMap,
                                             List<Pattern> include) throws NotFoundException {
        StringBuilder result = new StringBuilder();
        result.append("{");/*
        result.append("if(!multitier_log_agent.log_agent.util.TransformedFlag.flag){");
        result.append("multitier_log_agent.log_agent.util.TransformedFlag.flag=true;");*/
        result.append(LogClient.class.getName());
        result.append(".recordJoinpoint(");
        switch (drug_pattern) {
            case "pure":
                createJoinpointObject_pure_2(cc, result, m, "this", eventType, true, false);
                break;
            case "common":
                createJoinpointObject_common_2(cc, result, m, "this", eventType, true, false);
                break;
            case "param":
                createJoinpointObject_param_2(cc, result, m, "this", eventType, true, false);
                break;
            case "FUP":
                createJoinpointObject_FUP_2(cc, result, m, "this", eventType, true, false,methodMap,include);
                break;
        }
//        createJoinpointObject2(cc, result, m, "this", eventType, true, false);
        result.append(",\"");
        result.append(drug_pattern);
        result.append("\",");
        result.append(regionJavaString);
        result.append(");");/*
        result.append("}");
        result.append("else{multitier_log_agent.log_agent.util.TransformedFlag.flag=false;}");*/
        result.append("}");
        return result.toString();
    }
    public static String recordCommJoinpointSocket(CtBehavior m, EventType eventType,
            String socketObjectRef, String regionJavaString) {
        StringBuilder result = new StringBuilder();
        result.append("{");
        result.append(LogClient.class.getName());
        result.append(".recordCommJoinpoint(");
        createJoinpointObject(result, m, "this", eventType, false, false);
        result.append(",");
        createCommInfoObjectSocket(result, socketObjectRef);
        result.append(",");
        result.append(regionJavaString);
        result.append(");");
        result.append("}");
        return result.toString();
    }

    public static String recordCommJoinpointServlet(CtBehavior m, EventType eventType, String servletRequestRef, String regionJavaString) {
        StringBuilder result = new StringBuilder();
        result.append("{");
        result.append(LogClient.class.getName());
        result.append(".recordCommJoinpoint(");
        createJoinpointObject(result, m, "this", eventType, false, false);
        result.append(",");
        createCommInfoObjectServlet(result, servletRequestRef);
        result.append(",");
        result.append(regionJavaString);
        result.append(");");
        result.append("}");
        return result.toString();
    }

    public static String recordCallJoinpoint(Expr e, String longName, String targetRef, CtBehavior implMethod, EventType eventType, String regionJavaString, boolean includeParams, boolean includeReturn) {
        StringBuilder result = new StringBuilder();
        result.append("{");
        result.append(LogClient.class.getName());
        result.append(".recordCallJoinpoint(");
        createJoinpointCallObject(result, e, longName, targetRef, eventType, implMethod, includeParams, includeReturn);
        result.append(",");
        createCallInfoObject(result, implMethod);
        result.append(",");
        result.append(regionJavaString);
        result.append(");");
        result.append("}");
        return result.toString();
    }

    public static String recordExceptionThrowJoinpoint(CtBehavior m, String targetRef, EventType eventType, String regionJavaString) {
        StringBuilder result = new StringBuilder();
        result.append("{");
        result.append(LogClient.class.getName());
        result.append(".recordExceptionJoinpoint(");
        createJoinpointObject(result, m, targetRef, eventType, false, false);
        result.append(",");
        createExceptionThrowInfoObject(result);
        result.append(",");
        result.append(regionJavaString);
        result.append(");");
        result.append(" throw $e; ");
        result.append("}");
        return result.toString();
    }

    public static String recordExceptionHandleJoinpoint(Expr e, String longName, String targetRef, EventType eventType, String regionJavaString) {
        StringBuilder result = new StringBuilder();
        result.append("{");
        result.append(LogClient.class.getName());
        result.append(".recordExceptionJoinpoint(");
        createJoinpointCallObject(result, e, longName, targetRef, eventType, (CtBehavior)null, false, false);
        result.append(",");
        createExceptionHandleInfoObject(result);
        result.append(",");
        result.append(regionJavaString);
        result.append(");");
        result.append("}");
        return result.toString();
    }

    public static String recordFinallyHandleJoinpoint(Expr e, String longName, String targetRef, EventType eventType, String regionJavaString) {
        StringBuilder result = new StringBuilder();
        result.append("{");
        result.append(LogClient.class.getName());
        result.append(".recordExceptionJoinpoint(");
        createJoinpointCallObject(result, e, longName, targetRef, eventType, (CtBehavior)null, false, false);
        result.append(",");
        createFinallyExceptionHandleInfoObject(result);
        result.append(",");
        result.append(regionJavaString);
        result.append(");");
        result.append("}");
        return result.toString();
    }

    //zhc新增
    public static String recordInterface(CtClass ctInterface, CtBehavior m, String className, String targetRef, EventType eventType,
                                         String regionJavaString, boolean includeParams, boolean includeReturn) throws NotFoundException {
        StringBuilder result = new StringBuilder();
        result.append("{");
        result.append(LogClient.class.getName());
        result.append(".recordJoinpoint(");
        createJoinpointObjectWhenInterface(ctInterface, result, m, className, targetRef, eventType, includeParams, includeReturn);
        result.append(",");
        result.append(regionJavaString);
        //logger.info("zzzzzzzzzzzzzzzzzz" + regionJavaString);
        result.append(");");
        result.append("}");
        return result.toString();
    }

    //zhc
    private static void createJoinpointObjectWhenInterface(CtClass ctInterface, StringBuilder result, CtBehavior m, String className,
                                             String targetRef, EventType eventType, boolean includeParams, boolean includeReturn) throws NotFoundException {
        result.append("new ");
        result.append(JoinpointInfo.class.getName());
        result.append("(");

        stringQuoted(result, ctInterface.getName(), true);
        stringQuoted(result, className, true);
        stringQuoted(result, m.getLongName(), true);
        stringQuoted(result, m.getDeclaringClass().getClassFile().getSourceFile(), true);

        result.append(m.getMethodInfo().getLineNumber(0));
        result.append(",");

        if (targetRef != null) {
            result.append("System.identityHashCode(");
            result.append(targetRef);
            result.append("),");
        } else {
            result.append("0,");
        }
        result.append("System.currentTimeMillis(),");
        result.append("System.nanoTime(),");

        result.append(EventType.class.getName().replace('$', '.'));
        result.append(".");
        result.append(eventType.name());
        result.append(", Thread.currentThread().getId()");

        //获取接口中的属性
        getAttributes(result, ctInterface);

        //获取接口中的方法
        getMethods(result, ctInterface);

        result.append(")");

    }

    //只返回trace的纯净版本
    private static void createJoinpointObject_pure(CtClass cc, StringBuilder result, CtBehavior m,
                                              String targetRef, EventType eventType,
                                                boolean includeParams, boolean includeReturn){
        result.append("new ");
        result.append(JoinpointInfo.class.getName());
        result.append("(");

        stringQuoted(result, cc.getName(), true);
        stringQuoted(result, m.getLongName(), true);
        stringQuoted(result, m.getDeclaringClass().getClassFile().getSourceFile(), true);

        result.append(m.getMethodInfo().getLineNumber(0));
        result.append(",");

        if (targetRef != null) {
            result.append("System.identityHashCode(");
            result.append(targetRef);
            result.append("),");
        } else {
            result.append("0,");
        }
        result.append("System.currentTimeMillis(),");
        result.append("System.nanoTime(),");
        result.append(EventType.class.getName().replace('$', '.'));
        result.append(".");
        result.append(eventType.name());
        result.append(",");
        result.append("Thread.currentThread().getId()");
        result.append(")");
    }

    //用来返回类信息的版本
    private static void createJoinpointObject_common(CtClass cc, StringBuilder result, CtBehavior m,
            String targetRef, EventType eventType, boolean includeParams, boolean includeReturn) throws NotFoundException {
        result.append("new ");
        result.append(JoinpointInfo.class.getName());
        result.append("(");

        stringQuoted(result, cc.getName(), true);
        stringQuoted(result, m.getLongName(), true);
        stringQuoted(result, m.getDeclaringClass().getClassFile().getSourceFile(), true);

        result.append(m.getMethodInfo().getLineNumber(0));
        result.append(",");

        if (targetRef != null) {
            result.append("System.identityHashCode(");
            result.append(targetRef);
            result.append("),");
        } else {
            result.append("0,");
        }
        result.append("System.currentTimeMillis(),");
        result.append("System.nanoTime(),");

        result.append(EventType.class.getName().replace('$', '.'));
        result.append(".");
        result.append(eventType.name());
        result.append(",");
        result.append("Thread.currentThread().getId()");

        //新增获得类的属性
        getAttributes(result, cc);
        //新增获得类的构造方法
        getConstructors(result, cc);
        //新增获得类的方法
        getMethods(result, cc);
        //新增获得类的父类
        getSuperClass(result, cc);
        //新增获得类的接口
        getInterfaces(result, cc);

        result.append(")");

    }

    //xq，用于取参数和返回值的信息
    public static void createJoinpointObject_param(CtClass cc, StringBuilder result, CtBehavior m,
                                              String targetRef, EventType eventType, boolean includeParams,
                                              boolean includeReturn) throws NotFoundException {
        result.append("new ");
        result.append(JoinpointInfo.class.getName());
        result.append("(");
        stringQuoted(result, cc.getName(), true);
        stringQuoted(result, m.getLongName(), true);
        stringQuoted(result, m.getDeclaringClass().getClassFile().getSourceFile(), true);
        result.append(m.getMethodInfo().getLineNumber(0));
        result.append(",");
        if (targetRef != null) {
            result.append("System.identityHashCode(");
            result.append(targetRef);
            result.append("),");
        } else {
            result.append("0,");
        }

        result.append("System.currentTimeMillis(),");
        result.append("System.nanoTime(),");
        result.append(EventType.class.getName().replace('$', '.'));
        result.append(".");
        result.append(eventType.name());
        result.append(",");
        result.append("Thread.currentThread().getId()");

        //加上args或者returnValue,调用构造函数
        //如果事件是方法运行开始时
        if (eventType.name().startsWith("CALL")) {
            getArguments(result, m);
        } else if (eventType.name().equals("RETURN")) {//如果是方法运行结束时
            boolean emptyMethod = m.isEmpty();
            getReturnData(result, emptyMethod);
        } else if (eventType.name().equals("RETURN_NEW")) {
            getReturnDataNew(result);
        }
        result.append(")");
    }

    //xq，用于获取FUP
    private static void createJoinpointObject_FUP(CtClass cc, StringBuilder result, CtBehavior m, String targetRef,
                                                  EventType eventType, boolean includeParams, boolean includeReturn,
                                                  Map<String, org.apache.bcel.classfile.Method> methodMap,
                                                  List<Pattern> include){
        result.append("new ");
        result.append(JoinpointInfo.class.getName());
        result.append("(");

        stringQuoted(result, cc.getName(), true);
        stringQuoted(result, m.getLongName(), true);
        stringQuoted(result, m.getDeclaringClass().getClassFile().getSourceFile(), true);

        result.append(m.getMethodInfo().getLineNumber(0));
        result.append(",");

        if (targetRef != null) {
            result.append("System.identityHashCode(");
            result.append(targetRef);
            result.append("),");
        } else {
            result.append("0,");
        }
        result.append("System.currentTimeMillis(),");
        result.append("System.nanoTime(),");
        result.append(EventType.class.getName().replace('$', '.'));
        result.append(".");
        result.append(eventType.name());
        result.append(",");
        result.append("Thread.currentThread().getId()");
        try {
            getFUP(result, cc, m, methodMap,include);
        } catch (NotFoundException e) {
            result.append(",\"\"");
        }
        getReturnType(result, m,cc);
        result.append(")");
    }


    //用来输出类信息以及输入输出数据的版本
    private static void createJoinpointObject_all(CtClass cc, StringBuilder result, CtBehavior m,
                                              String targetRef, EventType eventType, boolean includeParams, boolean includeReturn) throws NotFoundException {
        result.append("new ");
        result.append(JoinpointInfo.class.getName());
        result.append("(");

        stringQuoted(result, cc.getName(), true);
        stringQuoted(result, m.getLongName(), true);
        stringQuoted(result, m.getDeclaringClass().getClassFile().getSourceFile(), true);

        result.append(m.getMethodInfo().getLineNumber(0));
        result.append(",");

        if (targetRef != null) {
            result.append("System.identityHashCode(");
            result.append(targetRef);
            result.append("),");
        } else {
            result.append("0,");
        }
        result.append("System.currentTimeMillis(),");
        result.append("System.nanoTime(),");

        result.append(EventType.class.getName().replace('$', '.'));
        result.append(".");
        result.append(eventType.name());
        result.append(",");
        result.append("Thread.currentThread().getId()");
        result.append(",");
        if (!java.lang.reflect.Modifier.isStatic(m.getModifiers())||!java.lang.reflect.Modifier.isAbstract(m.getModifiers())){
            result.append("java.lang.String.valueOf(org.openjdk.jol.vm.VM.current().addressOf(this))");
        }
        else
            result.append("\"STATIC\"");
        getAttributes(result, cc);
        getConstructors(result, cc);
        getMethods(result, cc);
        getSuperClass(result, cc);
        getInterfaces(result, cc);
        if (eventType.name().startsWith("CALL")) {
            getArguments(result, m);
        } else if (eventType.name().equals("RETURN")) {//如果是方法运行结束时
            boolean emptyMethod = m.isEmpty();
            getReturnData(result, emptyMethod);
        } else if (eventType.name().equals("RETURN_NEW")) {
            getReturnDataNew(result);
        }
        result.append(")");
    }


    //只返回trace的纯净版本2
    private static void createJoinpointObject_pure_2(CtClass cc, StringBuilder result, CtBehavior m,
                                                   String targetRef, EventType eventType,
                                                   boolean includeParams, boolean includeReturn){
        result.append("new ");
        result.append(JoinpointInfo.class.getName());
        result.append("(");

        stringQuoted(result, cc.getName(), true);
        stringQuoted(result, m.getLongName(), true);
        stringQuoted(result, "Not Need", true);

        result.append(0);
        result.append(",");

        result.append("0,");

        result.append("System.currentTimeMillis(),");
        result.append("System.nanoTime(),");

        result.append(EventType.class.getName().replace('$', '.'));
        result.append(".");
        result.append(eventType.name());
        result.append(",");
        result.append("Thread.currentThread().getId()");
        result.append(")");
    }
    //xq，用于取参数和返回值的信息2
    public static void createJoinpointObject_param_2(CtClass cc, StringBuilder result, CtBehavior m,
                                                   String targetRef, EventType eventType, boolean includeParams,
                                                   boolean includeReturn) throws NotFoundException {
        result.append("new ");
        result.append(JoinpointInfo.class.getName());
        result.append("(");

        stringQuoted(result, cc.getName(), true);
        stringQuoted(result, m.getLongName(), true);
        stringQuoted(result, "Not Need", true);

        result.append(0);
        result.append(",");

        result.append("0,");

        result.append("System.currentTimeMillis(),");
        result.append("System.nanoTime(),");

        result.append(EventType.class.getName().replace('$', '.'));
        result.append(".");
        result.append(eventType.name());
        result.append(",");
        result.append("Thread.currentThread().getId()");
        //加上args或者returnValue,调用构造函数
        //如果事件是方法运行开始时
        if (eventType.name().startsWith("CALL")) {
            getArguments(result, m);
        } else if (eventType.name().equals("RETURN")) {//如果是方法运行结束时
            boolean emptyMethod = m.isEmpty();
            getReturnData(result, emptyMethod);
        } else if (eventType.name().equals("RETURN_NEW")) {
            getReturnDataNew(result);
        }
        result.append(")");
    }
    //同样是忘了用来干什么的版本,类信息版
    private static void createJoinpointObject_common_2(CtClass cc, StringBuilder result, CtBehavior m,
                                              String targetRef, EventType eventType, boolean includeParams, boolean includeReturn) throws NotFoundException {
        result.append("new ");
        result.append(JoinpointInfo.class.getName());
        result.append("(");

        stringQuoted(result, cc.getName(), true);
        stringQuoted(result, m.getLongName(), true);
        stringQuoted(result, "Not Need", true);

        result.append(0);
        result.append(",");

        result.append("0,");

        result.append("System.currentTimeMillis(),");
        result.append("System.nanoTime(),");

        result.append(EventType.class.getName().replace('$', '.'));
        result.append(".");
        result.append(eventType.name());
        result.append(",");
        result.append("Thread.currentThread().getId()");
        //新增获得类的属性
        getAttributes(result, cc);
        //新增获得类的构造方法
        getConstructors(result, cc);
        //新增获得类的方法
        getMethods(result, cc);
        //新增获得类的父类
        getSuperClass(result, cc);
        //新增获得类的接口
        getInterfaces(result, cc);
        result.append(")");

    }

    //FUP版本2
    private static void createJoinpointObject_FUP_2(CtClass cc, StringBuilder result, CtBehavior m,
                                                    String targetRef, EventType eventType,
                                                    boolean includeParams, boolean includeReturn,
                                                    Map<String, org.apache.bcel.classfile.Method> methodMap,
                                                    List<Pattern> include){
        result.append("new ");
        result.append(JoinpointInfo.class.getName());
        result.append("(");

        stringQuoted(result, cc.getName(), true);
        stringQuoted(result, m.getLongName(), true);
        stringQuoted(result, "Not Need", true);

        result.append(0);
        result.append(",");

        result.append("0,");

        result.append("System.currentTimeMillis(),");
        result.append("System.nanoTime(),");

        result.append(EventType.class.getName().replace('$', '.'));
        result.append(".");
        result.append(eventType.name());
        result.append(",");
        result.append("Thread.currentThread().getId()");
        try {
            getFUP(result, cc, m, methodMap,include);
        } catch (NotFoundException e) {
            result.append(",\"\"");
        }
        getReturnType(result, m,cc);
        result.append(")");
    }
    //忘了是用来干什么的版本
    private static void createJoinpointObject(StringBuilder result, CtBehavior m,
                                              String targetRef, EventType eventType, boolean includeParams, boolean includeReturn) {
        result.append("new ");
        result.append(JoinpointInfo.class.getName());
        result.append("(");

        stringQuoted(result, m.getLongName(), true);
        stringQuoted(result, m.getDeclaringClass().getClassFile().getSourceFile(), true);

        result.append(m.getMethodInfo().getLineNumber(0));
        result.append(",");

        if (targetRef != null) {
            result.append("System.identityHashCode(");
            result.append(targetRef);
            result.append("),");
        } else {
            result.append("0,");
        }
        result.append("System.currentTimeMillis(),");
        result.append("System.nanoTime(),");

        result.append(EventType.class.getName().replace('$', '.'));
        result.append(".");
        result.append(eventType.name());
        result.append(",");

        result.append("Thread.currentThread().getId()");

        result.append(")");
        if (includeParams) {
            createJoinpointSetParams(result, m);
        }
        if (includeReturn && (m instanceof CtMethod)) {
            createJoinpointSetReturn(result, (CtMethod) m);
        }
    }

    //目前直接搞调用线程的
    private static void createJoinpointCallObject(StringBuilder result,
            Expr e, String longName, String targetRef,
            EventType eventType,
            CtBehavior implMethod, boolean includeParams, boolean includeReturn) {
        result.append("new ");
        result.append(JoinpointInfo.class.getName());
        result.append("(");

        stringQuoted(result, longName, true);
        stringQuoted(result, e.getFileName(), true);

        result.append(e.getLineNumber());
        result.append(",");

        if (targetRef != null) {
            result.append("System.identityHashCode(");
            result.append(targetRef);
            result.append("),");
        } else {
            result.append("0,");
        }

        result.append("System.currentTimeMillis(),");
        result.append("System.nanoTime(),");

        result.append(EventType.class.getName().replace('$', '.'));
        result.append(".");
        result.append(eventType.name());
        result.append(",");

        result.append("Thread.currentThread().getId()");

        //getAttributes(result, e.get);

        result.append(")");

        if(PointcutUtil.patternsAccept(longName)){
        	System.out.println("callThreadStart()");
        	result.append(".setNewthreadid($0.getId())");
        }

        if (includeParams) {
            try {
                if (e instanceof ConstructorCall) {
                    createJoinpointSetParams(result, ((ConstructorCall) e).getConstructor());
                }else if (e instanceof MethodCall) {
                    createJoinpointSetParams(result, ((MethodCall) e).getMethod());
                } else if (e instanceof NewExpr) {
                    createJoinpointSetParams(result, ((NewExpr) e).getConstructor());
                }
            } catch (NotFoundException ex) {
                logger.error("Could not append params logging", ex);
            }
        }
        if (includeReturn) {
            try {
                if (e instanceof MethodCall) {
                    createJoinpointSetReturn(result, ((MethodCall) e).getMethod());
                }
            } catch (NotFoundException ex) {
                logger.error("Could not append params logging", ex);
            }
        }
    }

    private static void createJoinpointSetReturn(StringBuilder result, CtMethod m) {
        try {
            // return value
            CtClass type = m.getReturnType();
//            int mo = type.getModifiers();
//            String ret = type.getName();
            if (type != null && type != CtClass.voidType) {
                result.append(".setReturn(");
                result.append(m.getModifiers());

                result.append(",");
                stringQuoted(result, type.getName(),false);
//                toCodeRunValue(result, type, "$_");
                result.append(")");
            }
            if (type != null && type == CtClass.voidType) {
                result.append(".setReturn(");
                result.append(m.getModifiers());

                result.append(",");
                stringQuoted(result, "void",false);
                result.append(")");
            }
            if (type == null){
                result.append(".setReturn(");
                result.append(m.getModifiers());
                result.append(",");
                stringQuoted(result, "void",false);
                result.append(")");
            }
        } catch (NotFoundException ex) {
            logger.error("Could not append return logging", ex);
        }
    }

    private static void createJoinpointSetParams(StringBuilder result, CtBehavior m) {
        try {
            // param arguments
            CtClass[] types = m.getParameterTypes();
            if (types == null || types.length == 0) {
                return;
            }

            result.append(".setParams(");
//            result.append("new ");
//            result.append(datatypeTypeStr);
//            result.append("[] {");
            result.append("new String[] {");
            String sep = "";
            for (int i = 0; i < types.length - 1; i++) {
//                result.append(sep);
//                sep = ",";

                CtClass type = types[i];
//                result.append(datatypeTypeStr);
//                result.append(".");
//                result.append(toDataType(type).name());
                stringQuoted(result, type.getName(), true);
            }
            stringQuoted(result, types[types.length - 1].getName(), false);

            result.append("}, new long[] {");
            sep = "";
            for (int i = 0; i < types.length; i++) {
                result.append(sep);
                sep = ",";
                String param = "$" + (i + 1); // start from $1

                CtClass type = types[i];
                toCodeRunValue(result, type, param);
            }
            result.append("} )");
        } catch (NotFoundException e) {
            logger.error("Could not append params logging", e);
        }
    }

//    private static void getMethodsWhenInterface(StringBuilder result, ) throws NotFoundException {
//
//    }

    private static void getConstructors(StringBuilder result, CtClass cc) throws NotFoundException {
        result.append(",");
        CtConstructor[] ctConstructors = cc.getDeclaredConstructors();
        if (ctConstructors == null || ctConstructors.length == 0){
            result.append("new ");
            result.append(Method.class.getName());
            result.append("[0]");
            return;
        }
        result.append("new ");
        result.append(Method.class.getName());
        result.append("[]{");

        String sep = "";
        for (CtConstructor ctConstructor : ctConstructors){
            result.append(sep);
            result.append("new ");
            result.append(Method.class.getName());
            result.append("(");
            result.append(ctConstructor.getModifiers());
            result.append(",");
            stringQuoted(result, ctConstructor.getName(),true);
            CtClass[] parameterTypes = ctConstructor.getParameterTypes();
            if (parameterTypes.length > 0) {
                result.append("new ");
                result.append("String");
                result.append("[]{");
                String sep2 = "";
                for (CtClass ctClass : parameterTypes){
                    result.append(sep2);
                    stringQuoted(result, ctClass.getName(),false);
                    //result.append(ctClass.getName());
                    sep2 = ",";
                }
                result.append("}");
            } else {
                result.append("new String[0]");
            }
            result.append(")");
            sep = ",";
        }

        result.append("}");

    }

    private static void getSuperClass(StringBuilder result, CtClass cc) throws NotFoundException {
        result.append(",");
        CtClass superClass = cc.getSuperclass();
        if ("java.lang.Object".equals(superClass.getName())){
            result.append("new ");
            result.append(SuperClass.class.getName());
            result.append("()");
            return;
        } else {
            result.append("new ");
            result.append(SuperClass.class.getName());
            result.append("(");
            stringQuoted(result, superClass.getName(),false);
            getAttributes(result, superClass);
            getConstructors(result, superClass);
            getMethods(result, superClass);
            result.append(")");
        }
    }

    private static void getInterfaces(StringBuilder result, CtClass cc) throws NotFoundException {
        result.append(",");
        CtClass[] interfaces = cc.getInterfaces();
        if (interfaces == null || interfaces.length == 0){
            result.append("new ");
            result.append(SuperClass.class.getName());
            result.append("[0]");
            return;
        }else{
            result.append("new ");
            result.append(SuperClass.class.getName());
            result.append("[]{");
            String sep = "";
            for (CtClass in : interfaces){
                result.append(sep);
                result.append("new ");
                result.append(SuperClass.class.getName());
                result.append("(");
                stringQuoted(result, in.getName(),false);
                getAttributes(result, in);
                getMethods(result, in);

                result.append(")");
                sep = ",";
            }

            result.append("}");
        }

    }

    private static void getMethods(StringBuilder result, CtClass cc) throws NotFoundException {
        result.append(",");
        CtMethod[] ctMethods = cc.getDeclaredMethods();
        if (ctMethods == null || ctMethods.length == 0){
            result.append("new ");
            result.append(Method.class.getName());
            result.append("[0]");
            return;
        }
        result.append("new ");
        result.append(Method.class.getName());
        result.append("[]{");

        String sep = "";
        for (CtMethod ctMethod : ctMethods){
            result.append(sep);
            result.append("new ");
            result.append(Method.class.getName());
            result.append("(");
            result.append(ctMethod.getModifiers());
            result.append(",");
            stringQuoted(result, ctMethod.getReturnType().getName(),true);
            stringQuoted(result, ctMethod.getName(),true);
            CtClass[] parameterTypes = ctMethod.getParameterTypes();
            if (parameterTypes.length > 0) {
                result.append("new ");
                result.append("String");
                result.append("[]{");
                String sep2 = "";
                for (CtClass ctClass : parameterTypes){
                    result.append(sep2);
                    stringQuoted(result, ctClass.getName(),false);
                    //result.append(ctClass.getName());
                    sep2 = ",";
                }
                result.append("}");
            } else {
                result.append("new String[0]");
            }
            result.append(")");
            sep = ",";
        }

        result.append("}");
        //System.out.println(result);
    }

    private static void getAttributes(StringBuilder result, CtClass cc) throws NotFoundException {
        result.append(",");
        CtField[] fields = cc.getDeclaredFields();
        if (fields == null || fields.length == 0){
            result.append("new ");
            result.append(Attribute.class.getName());
            result.append("[0]");
            return;
        }
        result.append("new ");
        result.append(Attribute.class.getName());
        result.append("[]{");

        String sep = "";
        for (CtField field : fields){
            result.append(sep);
            result.append("new ");
            result.append(Attribute.class.getName());
            result.append("(");
            result.append(field.getModifiers());
            result.append(",");
            stringQuoted(result, field.getType().getName(),true);
            stringQuoted(result, field.getName(),false);
            result.append(")");
            sep = ",";
        }
        result.append("}");
    }


    private static void getArguments(StringBuilder result, CtBehavior cm) throws NotFoundException {
        result.append(",");
        CtClass[] parameters = cm.getParameterTypes();
        if (parameters != null && parameters.length != 0) {
            result.append(LogClientCodegen.class.getName());
            result.append(".appendArgs($args)");
        } else {
            result.append("new ");
            result.append(Argument.class.getName());
            result.append("[0]");
        }

    }
    private static void getReturnData(StringBuilder result,boolean emptyMethod) throws NotFoundException {
        result.append(",");
        result.append("new ");
        result.append(ReturnData.class.getName());
        result.append("(");
        result.append("$type.getTypeName()");
        result.append(",");
        if (!emptyMethod) {
            result.append(LogClientCodegen.class.getName());
            result.append(".appendReturnValue($_)");
        } else {
            result.append("\"null\"");
        }

        result.append(")");
        //如果返回类型是void，则开启另外一个函数
        // TODO:验证一下返回值是null以及返回类型是void的时候 $_与$r的输出是什么，以及能不能直接String（$_）这样输出
    }
    //构造函数的返回值和返回类型均用特殊符号表示
    private static void getReturnDataNew(StringBuilder result) throws NotFoundException {
        //TODO: 测试构造函数的返回值和返回类型是什么
        result.append(",");
        result.append("new ");
        result.append(ReturnData.class.getName());
        result.append("(");
        result.append("\"#\"");
        result.append(",");
        result.append("\"#\"");
        result.append(")");
        //如果返回类型是void，则开启另外一个函数
        // TODO:验证一下返回值是null以及返回类型是void的时候 $_与$r的输出是什么，以及能不能直接String（$_）这样输出
    }

    /**
     * 各种类型的返回值的函数
     * @param value
     * @return
     */
    public static String appendReturnValue(Object value) throws IllegalAccessException {
        return value == null ? "null" : getObjectString(value, new HashSet<String>(), 0);
    }
    public static String appendReturnValue(int value){
        return String.valueOf(value);
    }
    public static String appendReturnValue(float value){
        return String.valueOf(value);
    }
    public static String appendReturnValue(boolean value){
        return String.valueOf(value);
    }
    public static String appendReturnValue(short value){
        return String.valueOf(value);
    }
    public static String appendReturnValue(long value ){
        return String.valueOf(value);
    }
    public static String appendReturnValue(byte value){
        return String.valueOf(value);
    }
    public static String appendReturnValue(char value){
        return String.valueOf(value);
    }
    public static String appendReturnValue(double value ){
        System.out.println("遇到double");
        NumberFormat nf=NumberFormat.getInstance();
        nf.setMaximumFractionDigits(20);
        nf.setGroupingUsed(false);
        return nf.format(value);
    }
    public static Argument[] appendArgs(Object[] args) throws IllegalAccessException {
        if (args.length==0){
            return new Argument[0];
        }
        else{
            Argument[] arguments=new Argument[args.length];
            int i=0;
            for(Object arg : args){
                String value;
                String classname;
                if(arg==null){
                    value="null";
                    classname="null";
                }
                else{
                    value=getObjectString(arg,new HashSet<String>(),0);
                    classname=arg.getClass().getTypeName();
                }
                arguments[i++]=new Argument(classname,value);
            }
            return arguments;
        }
    }


    public static String getObjectString(Object object, Set<String> used_parameter, int depth){
        if (object!=null){
            if(used_parameter.contains(object.getClass().getName()) && depth>5){
                return "&&&usedClassType&&&";
            } else{
                if (object instanceof String){
                    return object.toString();
                } else if (object instanceof String[]) {
                    StringBuilder objdata=new StringBuilder();
                    used_parameter.add(object.getClass().getTypeName());
                    String[] strings=(String [])object;
                    objdata.append("##[");
                    String arraysep="";
                    for(int i=0;i<strings.length;i++){
                        objdata.append(arraysep);
                        objdata.append(getObjectString(strings[i],used_parameter,depth+1));
                        arraysep="#;";
                    }
                    objdata.append("##]");
                    return objdata.toString();
                }else if (object instanceof Integer) {
                    return object.toString();
                } else if (object instanceof Float) {
                    return object.toString();
                } else if (object instanceof Boolean) {
                    return object.toString();
                } else if (object instanceof Short) {
                    return object.toString();
                } else if (object instanceof Long) {
                    return object.toString();
                } else if (object instanceof Double) {
                    System.out.println("遇到double");
                    NumberFormat nf=NumberFormat.getInstance();
                    nf.setMaximumFractionDigits(20);
                    nf.setGroupingUsed(false);
                    return nf.format(object);
                } else if (object instanceof Byte) {
                    return object.toString();
                } else if (object instanceof Character) {
                    return object.toString();
                } else if(object instanceof int[]){//TODO:考察是否要加上@@{和@@}
                    StringBuilder objdata=new StringBuilder();
                    used_parameter.add(object.getClass().getTypeName());
                    int[] ints=(int [])object;
                    objdata.append("##[");
                    String arraysep="";
                    for(int i=0;i<ints.length;i++){
                        objdata.append(arraysep);
                        objdata.append(getObjectString(ints[i],used_parameter,depth+1));
                        arraysep="#;";
                    }
                    objdata.append("##]");
                    return objdata.toString();
                } else if(object instanceof float[]){
                    StringBuilder objdata=new StringBuilder();
                    used_parameter.add(object.getClass().getTypeName());
                    float[] floats=(float [])object;
                    objdata.append("##[");
                    String arraysep="";
                    for(int i=0;i<floats.length;i++){
                        objdata.append(arraysep);
                        objdata.append(getObjectString(floats[i],used_parameter,depth+1));
                        arraysep="#;";
                    }
                    objdata.append("##]");
                    return objdata.toString();
                } else if(object instanceof boolean[]){
                    StringBuilder objdata=new StringBuilder();
                    used_parameter.add(object.getClass().getTypeName());
                    boolean[] booleans=(boolean [])object;
                    objdata.append("##[");
                    String arraysep="";
                    for(int i=0;i<booleans.length;i++){
                        objdata.append(arraysep);
                        objdata.append(getObjectString(booleans[i],used_parameter,depth+1));
                        arraysep="#;";
                    }
                    objdata.append("##]");
                    return objdata.toString();
                } else if(object instanceof short[]){
                    StringBuilder objdata=new StringBuilder();
                    used_parameter.add(object.getClass().getTypeName());
                    short[] shorts=(short [])object;
                    objdata.append("##[");
                    String arraysep="";
                    for(int i=0;i<shorts.length;i++){
                        objdata.append(arraysep);
                        objdata.append(getObjectString(shorts[i],used_parameter,depth+1));
                        arraysep="#;";
                    }
                    objdata.append("##]");
                    return objdata.toString();
                } else if(object instanceof long[]){
                    StringBuilder objdata=new StringBuilder();
                    used_parameter.add(object.getClass().getTypeName());
                    long[] longs=(long [])object;
                    objdata.append("##[");
                    String arraysep="";
                    for(int i=0;i<longs.length;i++){
                        objdata.append(arraysep);
                        objdata.append(getObjectString(longs[i],used_parameter,depth+1));
                        arraysep="#;";
                    }
                    objdata.append("##]");
                    return objdata.toString();
                }else if(object instanceof double[]){
                    StringBuilder objdata=new StringBuilder();
                    used_parameter.add(object.getClass().getTypeName());
                    double[] doubles=(double [])object;
                    objdata.append("##[");
                    String arraysep="";
                    for(int i=0;i<doubles.length;i++){
                        objdata.append(arraysep);
                        objdata.append(getObjectString(doubles[i],used_parameter,depth+1));
                        arraysep="#;";
                    }
                    objdata.append("##]");
                    return objdata.toString();
                }else if(object instanceof byte[]){
                    StringBuilder objdata=new StringBuilder();
                    used_parameter.add(object.getClass().getTypeName());
                    byte[] bytes=(byte [])object;
                    objdata.append("##[");
                    String arraysep="";
                    for(int i=0;i<bytes.length;i++){
                        objdata.append(arraysep);
                        objdata.append(getObjectString(bytes[i],used_parameter,depth+1));
                        arraysep="#;";
                    }
                    objdata.append("##]");
                    return objdata.toString();
                }else if(object instanceof char[]){
                    StringBuilder objdata=new StringBuilder();
                    used_parameter.add(object.getClass().getTypeName());
                    char[] chars=(char [])object;
                    objdata.append("##[");
                    String arraysep="";
                    for(int i=0;i<chars.length;i++){
                        objdata.append(arraysep);
                        objdata.append(getObjectString(chars[i],used_parameter,depth+1));
                        arraysep="#;";
                    }
                    objdata.append("##]");
                    return objdata.toString();
                }else if(object.getClass().isArray()){
                    StringBuilder objdata=new StringBuilder();
                    if(depth==0){
                        objdata.append("@@{");
                    }
                    used_parameter.add(object.getClass().getTypeName());
                    Object[] array=(Object [])object;
                    objdata.append("##[");
                    String arraysep="";
                    for (Object o : array) {
                        objdata.append(arraysep);
                        objdata.append(getObjectString(o, used_parameter, depth + 1));
                        arraysep = "#;";
                    }
                    objdata.append("##]");
                    if(depth==0){
                        objdata.append("@@}");
                    }
                    return objdata.toString();
                }
                else {
                    StringBuilder objdata=new StringBuilder();
                    if(depth==0){
                        objdata.append("@@{");
                    }
                    else{
                        return "null";
//                        objdata.append("@@[");
                    }
                    Class<?> clazz = object.getClass();
                    used_parameter.add(clazz.getTypeName());
                    // 遍历往上获取父类，直至最后一个父类
                    for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
                        // 获取当前类所有的字段
                        Field[] field = clazz.getDeclaredFields();
                        String sp="";
                        for (Field f : field) {
                            objdata.append(sp);
                            try {
                            f.setAccessible(true);
                            objdata.append(f.getName());
                            objdata.append("@@,");
                            objdata.append(f.getGenericType().getTypeName());
                            objdata.append("@@,");
                            objdata.append(getObjectString(f.get(object),used_parameter,depth+1));
                            } catch (Exception e) {
                                objdata.append("&&&unAccessibleField&&&");
                            }
                            sp="@@;";
                        }
                    }
                    if(depth==0){
                        objdata.append("@@}");
                    }
                    else{
                        objdata.append("@@]");
                    }
                    String str=objdata.toString();
                    return objdata.toString();
                }
            }
        }
        else{
            return "null";
        }
    }

    private static void getFUP(StringBuilder result,CtClass cc,CtBehavior m,
                               Map<String, org.apache.bcel.classfile.Method> methodMap,List<Pattern> include) throws NotFoundException {
        //获取m的全名（包括参数）
        result.append(",").append("\"");
        StringBuilder m_name=new StringBuilder();
        m_name.append(m.getName()).append("(");
        String sep="";
        for(CtClass param:m.getParameterTypes()){
            m_name.append(sep);
            m_name.append(param.getName());
            sep=",";
        }
        m_name.append(")");
        //根据方法名获取bcel的方法对象
        org.apache.bcel.classfile.Method method=methodMap.getOrDefault(m_name.toString(),null);
        try {
            JavaClass clazz= Repository.lookupClass(cc.getName());
            ConstantPool constantPool=clazz.getConstantPool();
            if(method!=null){
                MethodGen mg = new MethodGen(method, clazz.getClassName(), new ConstantPoolGen(constantPool));
                MethodVisitor mvistor=new MethodVisitor(mg, clazz,include);
                Set<String>fup=mvistor.startField();
                sep="";
                for(String f:fup){
                    result.append(sep);
                    result.append(f);
                    sep=",";
                }
            }
        } catch (ClassNotFoundException ignored) {

        }
        result.append("\"");
    }

    private static void getReturnType(StringBuilder result,CtBehavior m,CtClass cc){
        result.append(",");
        if (m instanceof CtConstructor){
//            System.out.println("this is a init method:"+cc.getName());
            result.append("\"").append(cc.getName()).append("\"");
        }
        else if (m instanceof CtMethod) {
            CtMethod ctm = (CtMethod)m;
            CtClass returnType= null;
            try {
                returnType = ctm.getReturnType();
            } catch (NotFoundException e) {
                result.append("\"\"");
                return;
            }
            String returnstr=returnType.getName();
            if(returnstr.equals("void")){
                result.append("\"\"");
                return;
            }
//            System.out.println("this is a common method"+returnType.getName());
            result.append("\"").append(returnType.getName()).append("\"");
        }
    }


    private static String getTypeString(List<String> data_type){
        StringBuilder types=new StringBuilder();
        String sep="";
        for(String type : data_type){
            types.append(type).append(sep);
            sep="|";
        }
        return types.toString();
    }

    private static DataType toDataType(CtClass type) {
        if (type == CtClass.booleanType) {
            return DataType.Boolean;
        } else if (type == CtClass.byteType
            || type == CtClass.charType
            || type == CtClass.intType
            || type == CtClass.longType
            || type == CtClass.shortType) {
            return DataType.Int;
        } else if (type == CtClass.doubleType
            || type == CtClass.floatType) {
            return DataType.Float;
        } else {
            return DataType.ObjectId;
        }
    }

    private static void toCodeRunType(StringBuilder result, CtClass type, String paramRef) {
        if (type == CtClass.booleanType) {
            stringQuoted(result, "boolean", false);
        } else if (type == CtClass.byteType) {
            stringQuoted(result, "byte", false);
        } else if (type == CtClass.charType) {
            stringQuoted(result, "char", false);
        } else if (type == CtClass.intType) {
            stringQuoted(result, "int", false);
        } else if (type == CtClass.longType) {
            stringQuoted(result, "long", false);
        } else if (type == CtClass.shortType) {
            stringQuoted(result, "short", false);
        } else if (type == CtClass.doubleType) {
            stringQuoted(result, "double", false);
        } else if (type == CtClass.floatType) {
            stringQuoted(result, "float", false);
        } else {
            result.append(paramRef).append(".getClass().getTypeName()");
        }
    }

    private static void toCodeRunValue(StringBuilder result, CtClass type, String paramRef) {
        if (type == CtClass.booleanType) {
            result.append("(long) ")
                .append(LogClient.class.getName())
                .append(".bool2int(").append(paramRef).append(")");
        } else if (type == CtClass.byteType
            || type == CtClass.charType
            || type == CtClass.intType
            || type == CtClass.longType
            || type == CtClass.shortType) {
            result.append("(long) ")
                .append(paramRef);
        } else if (type == CtClass.doubleType) {
            result.append(Double.class.getName())
                .append(".doubleToRawLongBits(").append(paramRef).append(")");
        } else if (type == CtClass.floatType) {
            result.append(Double.class.getName())
            .append(".doubleToRawLongBits((double)").append(paramRef).append(")");
        } else {
            result.append("(long) ")
                .append("System.identityHashCode(").append(paramRef).append(")");
        }
    }

    private static void createCallInfoObject(StringBuilder result, CtBehavior implMethod) {
        result.append("new ");
        result.append(CallInfo.class.getName());
        result.append("(");

        if (!Modifier.isStatic(implMethod.getModifiers())) {
            result.append("System.identityHashCode(this)");
        } else {
            result.append("0");
        }
        result.append(",");

        stringQuoted(result, implMethod.getLongName(), true);
        stringQuoted(result, implMethod.getDeclaringClass().getClassFile().getSourceFile(), true);

        result.append(implMethod.getMethodInfo().getLineNumber(0));

        result.append(")");
    }

    private static void createExceptionThrowInfoObject(StringBuilder result) {
        result.append("new ");
        result.append(ExceptionInfo.class.getName());
        result.append("(");

        result.append("$e.getClass().getName(), ");
        stringQuoted(result, ExceptionInfo.NameUncaught, false);

        result.append(")");
    }

    private static void createExceptionHandleInfoObject(StringBuilder result) {
        result.append("new ");
        result.append(ExceptionInfo.class.getName());
        result.append("(");

        result.append("$1.getClass().getName(),");
        result.append("$type.getName()");

        result.append(")");
    }

    private static void createFinallyExceptionHandleInfoObject(
            StringBuilder result) {
        result.append("new ");
        result.append(ExceptionInfo.class.getName());
        result.append("(");

        result.append("\"" + ExceptionInfo.NameFinally + "\",");
        result.append("\"" + ExceptionInfo.NameFinally + "\"");

        result.append(")");
    }

    private static void createCommInfoObjectSocket(StringBuilder result,
            String socketObjectRef) {
        result.append("new ");
        result.append(CommInfo.class.getName());
        result.append("(");

        result.append(socketObjectRef);
        result.append(".getLocalAddress().getHostAddress(),");

        result.append(socketObjectRef);
        result.append(".getLocalPort(),");

        result.append(socketObjectRef);
        result.append(".getInetAddress().getHostAddress(),");

        result.append(socketObjectRef);
        result.append(".getPort()");

        result.append(")");
    }

    private static void createCommInfoObjectServlet(StringBuilder result,
            String servletRequestRef) {
        result.append("new ");
        result.append(CommInfo.class.getName());
        result.append("(");

        result.append(servletRequestRef);
        result.append(".getLocalAddr(),");

        result.append(servletRequestRef);
        result.append(".getLocalPort(),");

        result.append(servletRequestRef);
        result.append(".getRemoteAddr(),");

        result.append(servletRequestRef);
        result.append(".getRemotePort()");

        result.append(")");
    }

    private static void stringQuoted(StringBuilder result, String value, boolean endComma) {
        result.append("\"");
        result.append(value);
        result.append("\"");
        if (endComma) {
            result.append(",");
        }
    }

}
