package multitier_log_agent.log_server.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import jdk.nashorn.internal.runtime.regexp.joni.constants.Arguments;
import multitier_log_agent.log_server.model.Event;
import multitier_log_agent.log_shared.model.*;
import multitier_log_agent.log_shared.model.JoinpointInfo.EventType;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class OutputLogfile extends AbstractOutput {

    public static Logger logger = LogManager.getLogger(OutputLogfile.class);

    private static String PLACE = "|";  //属性分离
    private static final String EPLACE = "*"; //父类和接口中分离同上的属性
    private static final String XPLACE = "^"; //多个接口的分离符

    private PrintWriter pw;

    public OutputLogfile(HierarchicalConfiguration config) throws IOException {
        this(config.getString("file", "log.txt"));
    }

    public OutputLogfile(String fileName) throws IOException {
        File logFile = new File(fileName);
        pw = new PrintWriter(new FileWriter(logFile, true));
        logger.info("\tWriting txt logfile output to: " + logFile.getName());
    }

    @Override
    protected void newCase(Integer t) {
        // nop
    }

    @Override
    protected void newEvent(Event t) {
        pw.println(_format(t));
        pw.flush();
    }

    @Override
    protected void caseFinished(Integer t) {
        // nop
    }

    @Override
    protected void finish() {
        pw.close();
    }

    //将一个完整的类名分离出包、类
    private String[] decompose(String allClassName){
        String[] allName = allClassName.split("\\.");
        String className = allName[allName.length-1];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < allName.length-1; i++) {
            sb.append(allName[i] + ".");
        }
        sb.deleteCharAt(sb.length()-1);
        String packageName = String.valueOf(sb);
        return new String[]{packageName, className};
    }

    //将一个完整的方法名分离出包、类、简单方法名[形如main(java.lang.String)]
    private String[] decompose(String joinpoit, boolean isConstruction){
        String allNames = joinpoit.split("\\(")[0];
        String[] allName = allNames.split("\\.");
        String packageName = "";
        String className = "";
        String methodName = "";
        StringBuffer sb = new StringBuffer();
        methodName = allName[allName.length - 1] + "(" + joinpoit.split("\\(")[1];
        if (isConstruction){
            className = allName[allName.length - 1].split("\\(")[0];
            if (allName.length > 2){
                for (int i=0; i<=allName.length-3; i++){
                    sb.append(allName[i] + ".");
                }
                sb.append(allName[allName.length-2]);
            }else{
                sb.append(allName[0]);
            }
        }else {
            className = allName[allName.length - 2];
            if (allName.length > 3){
                for (int i=0; i<=allName.length-4; i++){
                    sb.append(allName[i] + ".");
                }
                sb.append(allName[allName.length-3]);
            }else{
                sb.append(allName[0]);
            }
        }
        packageName = String.valueOf(sb);
        return new String[]{packageName, className, methodName};
    }

    private String removeBracket(String s){
        return s.split("]")[0].split("\\[")[1];
    }

    private String _format(Event t) {
        String pattern=t.packet.getPattern();
        if(pattern.equals("param"))
            PLACE="@@|";
        // | : , -  *用来对父类、接口中的二次分类
    	StringBuilder b = new StringBuilder();
        b.append(t.packet.getNodeInfo().getTier()+PLACE+t.packet.getNodeInfo().getNode());
        String lifecycle=t.packet.getJoinpointInfo().getEventType().name();
        String tempLife = lifecycle.split("_")[0];
        //插装对象为普通方法
        if (lifecycle.equals(EventType.CALL.name()) || lifecycle.equals(EventType.RETURN.name())){
            b.append(PLACE + "COMMON"); //类型2

            //包+类+方法（名称+参数【形如main(java.lang.String)】）名  3 4 5
            printName(b, t.packet.getJoinpointInfo().getJoinpoint(), false);

            //生命周期6
            if ("CALL".equals(tempLife)){
                b.append(PLACE + "START");
            }else {
                b.append(PLACE + "COMPLETE");
            }

            b.append(PLACE + t.packet.getJoinpointInfo().getNanotime());  //时间戳7
            b.append(PLACE + t.packet.getJoinpointInfo().getThreadId());  //线程号8
            b.append(PLACE + t.packet.getJoinpointInfo().getObjectID());  //对象标识

            switch (pattern) {
                case "common":
                    printClassData(tempLife, b, t);
                    break;
                case "param":
                    printParamData(tempLife, b, t);
                    break;
                case "pure":
                    break;
                case "FUP":
                    b.append(PLACE + t.packet.getJoinpointInfo().getFUP());  //FUP
                    b.append(PLACE + t.packet.getJoinpointInfo().getReturnTypeFUP());  //return类型
                    break;
            }

            /*//根据生命周期输入方法（参数/返回）数据  TODO:原来的获取输入输出数据的代码
            if ("CALL".equals(tempLife)){
                printArguments(b, t.packet.getJoinpointInfo().getArgs(), PLACE);
            }
            else{
                printReturnData(b, t.packet.getJoinpointInfo().getReturnData(), PLACE);
            }*/

            /*//类的属性9  原来的获取类信息的代码
            printAttributes(b, t.packet.getJoinpointInfo().getAttributes(), PLACE);
            //类的构造方法10
            printConstructionMethods(b, t.packet.getJoinpointInfo().getConstructionMethods(), PLACE);
            //类的方法11
            printMethods(b, t.packet.getJoinpointInfo().getMethods(), PLACE);
            //类的父类信息12
            printSuperClass(b, t.packet.getJoinpointInfo().getSuperClass());
            //类的接口信息13
            printInterfaces(b, t.packet.getJoinpointInfo().getInterfaces());*/

        }

        //插装对象为构造方法
        else if (lifecycle.equals(EventType.CALL_NEW.name()) || lifecycle.equals(EventType.RETURN_NEW.name())){
            b.append(PLACE + "CONSTRUCTION"); //类型2
            //包+类+方法（名称+参数【形如main(java.lang.String)】）名  3 4 5
            printName(b, t.packet.getJoinpointInfo().getJoinpoint(), true);
            //生命周期6
            if ("CALL".equals(tempLife)){
                b.append(PLACE + "START");
            }else {
                b.append(PLACE + "COMPLETE");
            }
            b.append(PLACE + t.packet.getJoinpointInfo().getNanotime());  //时间戳7
            b.append(PLACE + t.packet.getJoinpointInfo().getThreadId());  //线程号8
            b.append(PLACE + t.packet.getJoinpointInfo().getObjectID());  //对象标识

            switch (pattern) {
                case "common":
                    printClassData(tempLife, b, t);
                    break;
                case "param":
                    printParamData(tempLife, b, t);
                    break;
                case "pure":
                    break;
                case "FUP":
                    b.append(PLACE + t.packet.getJoinpointInfo().getFUP());  //FUP
                    break;
            }
            //原本获取类信息或输入输出数据的代码放在这
        }
        //处理接口暂时无
        else if (lifecycle.equals(EventType.CALL_INTERFACE.name()) || lifecycle.equals(EventType.RETURN_INTERFACE.name())){
            b.append(PLACE + "INTERFACE"); //类型2
            //包+类+方法（名称+参数【形如main(java.lang.String)】）名  3 4 5
            printName(b, t.packet.getJoinpointInfo().getJoinpoint(), false);
            //生命周期6
            if ("CALL".equals(tempLife)){
                b.append(PLACE + "START");
            }else {
                b.append(PLACE + "COMPLETE");
            }
            b.append(PLACE + t.packet.getJoinpointInfo().getNanotime());  //时间戳7
            b.append(PLACE + t.packet.getJoinpointInfo().getThreadId());  //线程号8

            //接口中的属性9
            b.append(PLACE);
            if (t.packet.getJoinpointInfo().getAttributes() != null){
                for (Attribute attribute : t.packet.getJoinpointInfo().getAttributes()){
                    String modifier = "";
                    if (attribute.getModifier() == 0){
                        modifier = "default";
                    }else{
                        modifier = Modifier.toString(attribute.getModifier());
                    }
                    b.append(modifier + "," +
                            attribute.getType() + "," +
                            attribute.getName() + ";");
                }
            }
            //接口中的方法10
            b.append(PLACE);
            if (t.packet.getJoinpointInfo().getMethods() != null){
                for (Method method : t.packet.getJoinpointInfo().getMethods()){
                    String modifier = "";
                    if (method.getModifier() == 0){
                        modifier = "default";
                    }else{
                        modifier = Modifier.toString(method.getModifier());
                    }
                    StringBuffer sb2 = new StringBuffer();
                    String[] paramTypes = method.getParamType();
                    if (paramTypes.length > 0){
                        for (String paramType : paramTypes) {
                            sb2.append(paramType + "-");
                        }
                        sb2.deleteCharAt(sb2.length()-1);
                    }
                    b.append(modifier + "," +
                            method.getType() + "," +
                            method.getName() + "," +
                            sb2
                    );
                    b.append(";");
                }
                //b.deleteCharAt(b.length() - 1);
            }
        }
        //新线程
        else if (EventType.RETURN_THREAD.name().equals(lifecycle) || EventType.CALL_THREAD.name().equals(lifecycle)){
            b.append(PLACE + "THREAD"); //类型2
            //包+类+方法（名称+参数【形如main(java.lang.String)】）名  3 4 5
            printName(b, t.packet.getJoinpointInfo().getJoinpoint(), false);

            //生命周期6
            if ("CALL".equals(tempLife)){
                b.append(PLACE + "START");
            }else {
                b.append(PLACE + "COMPLETE");
            }
            b.append(PLACE + t.packet.getJoinpointInfo().getNanotime());  //时间戳7
            b.append(PLACE + t.packet.getJoinpointInfo().getThreadId());  //线程号8
            b.append(PLACE + t.packet.getJoinpointInfo().getNewthreadid());  //新线程号9
        }
        //通信
        else if (EventType.CALL_SERVLET.name().equals(lifecycle) || EventType.RETURN_SERVLET.name().equals(lifecycle) ||
                EventType.CALL_SOCKET.name().equals(lifecycle) || EventType.RETURN_SOCKET.name().equals(lifecycle)){
            b.append(PLACE + "COMMUNICATION"); //类型2
            //包+类+方法（名称+参数【形如main(java.lang.String)】）名  3 4 5
            printName(b, t.packet.getJoinpointInfo().getJoinpoint(), false);

            //生命周期6
            if ("CALL".equals(tempLife)){
                b.append(PLACE + "START");
            }else {
                b.append(PLACE + "COMPLETE");
            }
            b.append(PLACE + t.packet.getJoinpointInfo().getNanotime());  //时间戳7
            b.append(PLACE + t.packet.getJoinpointInfo().getThreadId());  //线程号8
            b.append(PLACE + t.packet.getCommInfo().getLocalHost()+":"+t.packet.getCommInfo().getLocalPort()+","
                    +t.packet.getCommInfo().getRemoteHost()+":"+t.packet.getCommInfo().getRemotePort()); //通信资源9
        }
        //可能之后加的异常
        else{

        }

    	return b.toString();

    }

    private void printInterfaces(StringBuilder b, SuperClass[] interfaces) {
        b.append(PLACE);
        String sep="";
        if (interfaces != null){
            for (SuperClass inter : interfaces){
                b.append(sep);
                String[] message = decompose(inter.getClassName());
                //接口包名0
                b.append(message[0]);
                //接口名1
                b.append(EPLACE);
                b.append(message[1]);
                //接口属性2
                printAttributes(b, inter.getAttributes(), EPLACE);
                //接口方法3
                printMethods(b, inter.getMethods(), EPLACE);
                sep=XPLACE;
            }
        }

    }

    private void printSuperClass(StringBuilder b, SuperClass superClass) {
        b.append(PLACE);
        if (superClass != null && superClass.getClassName().length() > 0){
            String[] message = decompose(superClass.getClassName());
            //父类包名0
            b.append(message[0]);
            //父类类名1
            b.append(EPLACE);
            b.append(message[1]);
            //父类属性2
            printAttributes(b, superClass.getAttributes(), EPLACE);
            //父类构造3
            printConstructionMethods(b, superClass.getConstructionMethods(), EPLACE);
            //父类方法4
            printMethods(b, superClass.getMethods(), EPLACE);
        }
    }

    private void printMethods(StringBuilder b, Method[] methods, String place) {
        b.append(place);
        String sep="";
        if (methods != null){
            for (Method method : methods){
                b.append(sep);
                String modifier = "";
                if (method.getModifier() == 0){
                    modifier = "default";
                }else{
                    modifier = Modifier.toString(method.getModifier());
                }
                StringBuffer sb2 = new StringBuffer();
                String[] paramTypes = method.getParamType();
                if (paramTypes.length > 0){
                    for (String paramType : paramTypes) {
                        sb2.append(paramType + "-");
                    }
                    sb2.deleteCharAt(sb2.length()-1);
                }
                b.append(modifier + "," +
                        method.getType() + "," +
                        method.getName() + "," +
                        sb2
                );
                sep=";";
            }
        }
    }

    private void printConstructionMethods(StringBuilder b, Method[] constructionMethods, String place) {
        b.append(place);
        String sep="";
        if (constructionMethods != null){
            for (Method constructionMethod : constructionMethods){
                b.append(sep);
                String modifier = "";
                if (constructionMethod.getModifier() == 0){
                    modifier = "default";
                }else{
                    modifier = Modifier.toString(constructionMethod.getModifier());
                }
                StringBuffer sb2 = new StringBuffer();
                String[] paramTypes = constructionMethod.getParamType();
                if (paramTypes.length > 0){
                    for (String paramType : paramTypes) {
                        sb2.append(paramType + "-");
                    }
                    sb2.deleteCharAt(sb2.length()-1);
                }
                b.append(modifier + "," +
                        constructionMethod.getName() + "," +
                        sb2
                );
                sep=";";
            }
        }
    }

    private void printAttributes(StringBuilder b, Attribute[] attributes, String place) {
        b.append(place);
        String sep="";
        if (attributes != null){
            for (Attribute attribute : attributes){
                b.append(sep);
                String modifier = "";
                if (attribute.getModifier() == 0){
                    modifier = "default";
                }else{
                    modifier = Modifier.toString(attribute.getModifier());
                }
                b.append(modifier + "," +
                        attribute.getType() + "," +
                        attribute.getName());
                sep=";";
            }
        }
    }

    private void printName(StringBuilder b, String joinpoint, boolean isConstruction) {
        String[] message = decompose(joinpoint, isConstruction);
        b.append(PLACE + message[0]);  //包名3
        b.append(PLACE + message[1]);  //类名4
        b.append(PLACE + message[2]);  //方法名5
    }

    private void printArguments(StringBuilder b, Argument[] arguments, String place){
        b.append(place);
        if (arguments != null){
            String sep = "";
            for(Argument argument :arguments){
                b.append(sep);
                b.append(argument.getType() + "@@," +
                        argument.getValue());
                sep="@@;";
            }
        }
        else{
            b.append("NOTHING");
        }
        b.append(place+place);
    }
    private void printReturnData(StringBuilder b, ReturnData returnData, String place){
        b.append(place);
        if (returnData != null){
            b.append(returnData.getType() + "@@," +
                     returnData.getValue() + "");
        }
        else{
            b.append("NOTHING");
        }
        b.append(place+place);
    }

    private void printParamData(String tempLife,StringBuilder b,Event t){
        if ("CALL".equals(tempLife)){
            printArguments(b, t.packet.getJoinpointInfo().getArgs(), PLACE);
        }
        else{
            printReturnData(b, t.packet.getJoinpointInfo().getReturnData(), PLACE);
        }
    }

    private void printClassData(String tempLife,StringBuilder b,Event t){
        //类的属性9
        printAttributes(b, t.packet.getJoinpointInfo().getAttributes(), PLACE);
        //类的构造方法10
        printConstructionMethods(b, t.packet.getJoinpointInfo().getConstructionMethods(), PLACE);
        //类的方法11
        printMethods(b, t.packet.getJoinpointInfo().getMethods(), PLACE);
        //类的父类信息12
        printSuperClass(b, t.packet.getJoinpointInfo().getSuperClass());
        //类的接口信息13
        printInterfaces(b, t.packet.getJoinpointInfo().getInterfaces());
    }
}
