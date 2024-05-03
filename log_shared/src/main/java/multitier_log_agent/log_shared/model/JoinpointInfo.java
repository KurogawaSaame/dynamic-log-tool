package multitier_log_agent.log_shared.model;

import java.io.*;
import java.util.*;

public class JoinpointInfo implements Externalizable {
    public static final int WRITE_READ_UTF_MAX_LENGTH = 21843;
    public static enum EventType {
        CALL, RETURN,  //普通函数
        CALL_NEW, RETURN_NEW, //构造函数
        CALL_INTERFACE, RETURN_INTERFACE,  //接口调用
        CALL_THREAD, RETURN_THREAD,  //线程调用
        CALL_SERVLET, RETURN_SERVLET,  //servlet调用
        CALL_SOCKET, RETURN_SOCKET,  //socket
        THROW, HANDLE;  //暂时没用
    }

    public static enum DataType {
        ObjectId,       // system hash code
        Int,            // 64 bit int (long)
        Float,          // 64 bit float (double)
        Boolean         // boolean value
    }

    private String joinpoint;
    private String filename;
    private int linenr;
    private int idHashCode;

//    private DataType[] paramTypes;
    private String[] paramTypes;
//    private String[] paramRunTypes;
    private long[] paramValues;

    private int returnModifier;
    private String returnType;
//    private DataType returnType;
//    private String returnRunType;
    private long returnValue;

    private long timestamp;
    private long nanotime;
    private EventType eventType;
    private long threadid;
    private long newthreadid;

    //以下为zhc添加
    private String className;
    private SuperClass superClass;
    private SuperClass[] interfaces;
    private Attribute[] attributes;
    private Method[] constructionMethods;
    private Method[] methods;

    //以下为xq添加&修改
    private Argument[] args;
    private ReturnData returnData;
    private String objectID="NULL"; //
    private String FUP="";
    private String returnType_FUP;

    public JoinpointInfo() {

    }

    //普通方法和构造方法
    public JoinpointInfo(String className, String joinpoint, String filename, int linenr, int idHashCode,
            long timestamp, long nanotime, EventType eventType, long threadId, Attribute[] attributes,
                         Method[] constructionMethods, Method[] methods, SuperClass superClass, SuperClass[] interfaces) {
        this.className = className;
        this.joinpoint = joinpoint;
        this.filename = filename;
        this.linenr = linenr;
        this.timestamp = timestamp;
        this.nanotime = nanotime;
        this.eventType = eventType;
        this.threadid = threadId;
        this.idHashCode = idHashCode;
        this.attributes = attributes;
        this.constructionMethods = constructionMethods;
        this.methods = methods;
        this.superClass = superClass;
        this.interfaces = interfaces;
    }

    //xq，包含传入参数（事件为start）的构造方法
    public JoinpointInfo(String className, String joinpoint, String filename, int linenr, int idHashCode,
                         long timestamp, long nanotime, EventType eventType, long threadId,
                         Argument[] args) {
        this.className = className;
        this.joinpoint = joinpoint;
        this.filename = filename;
        this.linenr = linenr;
        this.timestamp = timestamp;
        this.nanotime = nanotime;
        this.eventType = eventType;
        this.threadid = threadId;
        this.idHashCode = idHashCode;
        this.args=args;
    }
    //xq，包含返回值（事件为end）的构造方法
    public JoinpointInfo(String className, String joinpoint, String filename, int linenr, int idHashCode,
                         long timestamp, long nanotime, EventType eventType, long threadId, ReturnData returnData) {
        this.className = className;
        this.joinpoint = joinpoint;
        this.filename = filename;
        this.linenr = linenr;
        this.timestamp = timestamp;
        this.nanotime = nanotime;
        this.eventType = eventType;
        this.threadid = threadId;
        this.idHashCode = idHashCode;
        this.returnData = returnData;
    }
    //pure纯净版
    public JoinpointInfo(String className, String joinpoint, String filename, int linenr, int idHashCode,
                         long timestamp, long nanotime, EventType eventType, long threadId) {
        this.className = className;
        this.joinpoint = joinpoint;
        this.filename = filename;
        this.linenr = linenr;
        this.timestamp = timestamp;
        this.nanotime = nanotime;
        this.eventType = eventType;
        this.threadid = threadId;
        this.idHashCode = idHashCode;
    }
    //FUP版
    public JoinpointInfo(String className, String joinpoint, String filename, int linenr, int idHashCode,
                         long timestamp, long nanotime, EventType eventType, long threadId,String fup) {
        this.className = className;
        this.joinpoint = joinpoint;
        this.filename = filename;
        this.linenr = linenr;
        this.timestamp = timestamp;
        this.nanotime = nanotime;
        this.eventType = eventType;
        this.threadid = threadId;
        this.idHashCode = idHashCode;
        this.FUP=fup;
    }
    //FUP版PLUS,添加了返回类型
    public JoinpointInfo(String className, String joinpoint, String filename, int linenr, int idHashCode,
                         long timestamp, long nanotime, EventType eventType, long threadId,String fup,String returnType_FUP) {
        this.className = className;
        this.joinpoint = joinpoint;
        this.filename = filename;
        this.linenr = linenr;
        this.timestamp = timestamp;
        this.nanotime = nanotime;
        this.eventType = eventType;
        this.threadid = threadId;
        this.idHashCode = idHashCode;
        this.FUP=fup;
        this.returnType_FUP=returnType_FUP;
    }
    public JoinpointInfo(String joinpoint, String filename, int linenr, int idHashCode,
                         long timestamp, long nanotime, EventType eventType, long threadId) {
        this.joinpoint = joinpoint;
        this.filename = filename;
        this.linenr = linenr;
        this.timestamp = timestamp;
        this.nanotime = nanotime;
        this.eventType = eventType;
        this.threadid = threadId;
        this.idHashCode = idHashCode;
    }

    public JoinpointInfo(String joinpoint, String filename, int linenr, int idHashCode,
                         long timestamp, long nanotime, EventType eventType, long threadId, Attribute[] attributes,
                         Method[] constructionMethods, Method[] methods) {
        this.joinpoint = joinpoint;
        this.filename = filename;
        this.linenr = linenr;
        this.timestamp = timestamp;
        this.nanotime = nanotime;
        this.eventType = eventType;
        this.threadid = threadId;
        this.idHashCode = idHashCode;
        this.attributes = attributes;
        this.constructionMethods = constructionMethods;
        this.methods = methods;
    }

    //接口
    public JoinpointInfo(String className, String joinpoint, String filename, int linenr, int idHashCode,
                         long timestamp, long nanotime, EventType eventType, long threadId, Attribute[] attributes, Method[] methods) {
        this.className = className;
        this.joinpoint = joinpoint;
        this.filename = filename;
        this.linenr = linenr;
        this.timestamp = timestamp;
        this.nanotime = nanotime;
        this.eventType = eventType;
        this.threadid = threadId;
        this.idHashCode = idHashCode;
        this.attributes = attributes;
        this.methods = methods;
    }

    public JoinpointInfo setParams(String[] paramType, long[] paramValues) {
        this.paramTypes = paramType;
        this.paramValues = paramValues;
        return this;
    }

    public JoinpointInfo setReturn(int modifier, String returnType, long returnValue) {
        this.returnModifier = modifier;
        this.returnType = returnType;
        this.returnValue = returnValue;
        return this;
    }

    //针对void方法
    public JoinpointInfo setReturn(int modifier, String returnType) {
        this.returnModifier = modifier;
        this.returnType = returnType;
        this.returnValue = (long)0;
        //System.out.println("构造方法：" + joinpoint + ":" + returnModifier);
        return this;
    }

    public int getReturnModifier() {
        return returnModifier;
    }

    public String getReturnType() {
        return returnType;
    }

    //long修改成了Object
    public Object getReturnValue() {
        return returnValue;
    }

    public String[] getParamTypes() {
        return paramTypes;
    }

    public long[] getParamValues() {
        return paramValues;
    }

    public String getJoinpoint() {
        return joinpoint;
    }

    public String getFilename() {
        return filename;
    }

    public int getLinenr() {
        return linenr;
    }

    public int getIdHashCode() {
        return idHashCode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getNanotime() {
        return nanotime;
    }

    public EventType getEventType() {
        return eventType;
    }

    public long getThreadId() {
        return threadid;
    }

    public long getNewthreadid() {
		return newthreadid;
	}

    public String getClassName() {
        return className;
    }

    public SuperClass getSuperClass() {
        return superClass;
    }

    public SuperClass[] getInterfaces() { return interfaces; }

    public Attribute[] getAttributes() { return attributes; }

    public Method[] getMethods() { return methods; }

    public Method[] getConstructionMethods() { return constructionMethods; }

    public Argument[] getArgs(){return args;}

    public ReturnData getReturnData(){return returnData;}

    public String getFUP() {
        return FUP;
    }
    public String getReturnTypeFUP() {
        return returnType_FUP;
    }
    public JoinpointInfo setNewthreadid(long newthreadid) {
		this.newthreadid = newthreadid;
		//System.out.println("setNewthreadid:"+this.newthreadid);
		return this;
	}

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    //向sever传输不包含其他数据的trace日志信息
    public void writeExternal_0(ObjectOutput out) throws IOException {
        out.writeUTF(joinpoint);
        out.writeUTF(filename);
        out.writeInt(linenr);
        out.writeInt(idHashCode);
        out.writeLong(timestamp);
        out.writeLong(nanotime);
        out.writeInt(eventType.ordinal());
        out.writeLong(threadid);
        out.writeLong(newthreadid);
        if (className != null){
            out.writeUTF(className);
        }else{
            out.writeUTF("null");
        }
        out.writeUTF(objectID);
    }
	//向sever传输类信息的日志数据
	public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(joinpoint);
        out.writeUTF(filename);
        out.writeInt(linenr);
        out.writeInt(idHashCode);
        out.writeLong(timestamp);
        out.writeLong(nanotime);
        out.writeInt(eventType.ordinal());
        out.writeLong(threadid);
        out.writeLong(newthreadid);
        if (className != null){
            out.writeUTF(className);
        }else{
            out.writeUTF("null");
        }
        out.writeUTF(objectID);

        writeAttributes(out, attributes);
        writeConstructionMethods(out, constructionMethods);
        writeMethods(out, methods);
        writeSuperClass(out, superClass);
        writeInterfaces(out, interfaces);
    }
    //xq,向server传输关于参数和返回值的日志信息
    public void writeExternal2(ObjectOutput out) throws IOException {
        out.writeUTF(joinpoint);
        out.writeUTF(filename);
        out.writeInt(linenr);
        out.writeInt(idHashCode);
        out.writeLong(timestamp);
        out.writeLong(nanotime);
        out.writeInt(eventType.ordinal());
        out.writeLong(threadid);
        out.writeLong(newthreadid);
        if (className != null){
            out.writeUTF(className);
        }else{
            out.writeUTF("null");
        }
        out.writeUTF(objectID);

        writeArguments(out,args);
        writeReturnData(out,returnData);
    }
    //用于传输FUP数据
    public void writeExternal_FUP(ObjectOutput out) throws IOException {
        out.writeUTF(joinpoint);
        out.writeUTF(filename);
        out.writeInt(linenr);
        out.writeInt(idHashCode);
        out.writeLong(timestamp);
        out.writeLong(nanotime);
        out.writeInt(eventType.ordinal());
        out.writeLong(threadid);
        out.writeLong(newthreadid);
        if (className != null){
            out.writeUTF(className);
        }else{
            out.writeUTF("null");
        }
        out.writeUTF(objectID);
        out.writeUTF(FUP);
        out.writeUTF(returnType_FUP);
    }

    private void writeInterfaces(ObjectOutput out, SuperClass[] interfaces) throws IOException {
        if (interfaces != null && interfaces.length > 0){
            out.writeInt(interfaces.length);
            for (SuperClass inter : interfaces) {
                out.writeUTF(inter.getClassName());
                writeAttributes(out, inter.getAttributes());
                writeMethods(out, inter.getMethods());
            }
        } else {
            out.writeInt(0);
        }
    }

    private void writeSuperClass(ObjectOutput out, SuperClass superClass) throws IOException {
        if (superClass != null && superClass.getClassName() != null && superClass.getClassName().length() > 0){
            out.writeUTF(superClass.getClassName());
            writeAttributes(out, superClass.getAttributes());
            writeConstructionMethods(out, superClass.getConstructionMethods());
            writeMethods(out, superClass.getMethods());
        }else {
            out.writeUTF("null");
        }
    }

    private void writeMethods(ObjectOutput out, Method[] methods) throws IOException {
        if (methods == null || methods.length == 0){
            out.writeInt(0);
        }else{
            out.writeInt(methods.length);
            for (int i = 0; i < methods.length; i++) {
                out.writeInt(methods[i].getModifier());
                out.writeUTF(methods[i].getType());
                out.writeUTF(methods[i].getName());
                if (methods[i].getParamType() == null || methods[i].getParamType().length == 0){
                    out.writeInt(0);
                }else {
                    out.writeInt(methods[i].getParamType().length);
                    for (String paramType: methods[i].getParamType()) {
                        out.writeUTF(paramType);
                    }
                }
            }
        }
    }

    private void writeConstructionMethods(ObjectOutput out, Method[] constructionMethods) throws IOException {
        if (constructionMethods == null || constructionMethods.length == 0){
            out.writeInt(0);
        }else{
            out.writeInt(constructionMethods.length);
            for (int i = 0; i < constructionMethods.length; i++) {
                out.writeInt(constructionMethods[i].getModifier());
                out.writeUTF(constructionMethods[i].getName());
                if (constructionMethods[i].getParamType() == null || constructionMethods[i].getParamType().length == 0){
                    out.writeInt(0);
                }else {
                    out.writeInt(constructionMethods[i].getParamType().length);
                    for (String paramType: constructionMethods[i].getParamType()) {
                        out.writeUTF(paramType);
                    }
                }
            }
        }
    }

    private void writeAttributes(ObjectOutput out, Attribute[] attributes) throws IOException {
        if (attributes == null || attributes.length == 0){
            out.writeInt(0);
        }else{
            out.writeInt(attributes.length);
            for (int i = 0; i < attributes.length; i++) {
                out.writeInt(attributes[i].getModifier());
                out.writeUTF(attributes[i].getType());
                out.writeUTF(attributes[i].getName());
                //System.out.println("JoinpointInfo:writeAttributes:" + attributes[i].getName());
            }
        }
    }


    private void writeArguments(ObjectOutput out,Argument[] args) throws IOException {
        if (args == null || args.length == 0){
            out.writeInt(0);
        }else{
            out.writeInt(args.length);
            for (int i = 0; i < args.length; i++) {
                out.writeUTF(args[i].getType());
                String value=args[i].getValue();
                if (value.length() > WRITE_READ_UTF_MAX_LENGTH){
                    int count=1;
                    if(value.length()%WRITE_READ_UTF_MAX_LENGTH==0){
                        count=value.length()/WRITE_READ_UTF_MAX_LENGTH;
                    }
                    else{
                        count=value.length()/WRITE_READ_UTF_MAX_LENGTH+1;
                    }
                    out.writeInt(count);
                    for (int j = 1; j < count+1; j++) {
                        try {
                            out.writeUTF(value.substring(WRITE_READ_UTF_MAX_LENGTH*(j-1),Math.min(WRITE_READ_UTF_MAX_LENGTH*j,value.length())));
                        }
                        catch (Exception e){
                            System.out.print(WRITE_READ_UTF_MAX_LENGTH*(j-1));
                            System.out.print("        ");
                            System.out.print(Math.min(WRITE_READ_UTF_MAX_LENGTH*j,value.length()));
                            System.out.print("        ");
                            System.out.print(value.length());
                            System.out.print("        ");
                            System.out.print(value.getBytes().length);
                        }
                    }
                }
                else{
                    out.writeInt(1);
                    out.writeUTF(value);
                }
            }
        }
    }

    private void writeReturnData(ObjectOutput out,ReturnData returnData) throws IOException {
        if(returnData==null){
            out.writeInt(0);
        }
        //反正不论有没有返回值，不论是不是构造方法，returnData都有相对应的对象实例，那就全部传入
        else{
            out.writeInt(1);
            out.writeUTF(returnData.getType());
            String value=returnData.getValue();
            int count= 1;
            if(value.length()%WRITE_READ_UTF_MAX_LENGTH==0){
                count=value.length()/WRITE_READ_UTF_MAX_LENGTH;
            }
            else{
                count=value.length()/WRITE_READ_UTF_MAX_LENGTH+1;
            }
            out.writeInt(count);
            for (int j = 1; j < count+1; j++) {
                out.writeUTF(value.substring(WRITE_READ_UTF_MAX_LENGTH*(j-1), Math.min(WRITE_READ_UTF_MAX_LENGTH * j, value.length())));
            }
//            out.writeUTF(returnData.getValue());
        }
    }

    public void readExternal_0(ObjectInput in) throws IOException,
            ClassNotFoundException{
        joinpoint = in.readUTF();
        filename = in.readUTF();
        linenr = in.readInt();
        idHashCode = in.readInt();
        timestamp = in.readLong();
        nanotime = in.readLong();
        eventType = EventType.values()[in.readInt()];
        threadid = in.readLong();
        this.newthreadid=in.readLong();
        className = in.readUTF();
        this.objectID=in.readUTF();
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        joinpoint = in.readUTF();
        filename = in.readUTF();
        linenr = in.readInt();
        idHashCode = in.readInt();
        timestamp = in.readLong();
        nanotime = in.readLong();
        eventType = EventType.values()[in.readInt()];
        threadid = in.readLong();
        this.newthreadid=in.readLong();
        className = in.readUTF();
        this.objectID=in.readUTF();

        this.attributes = readAttributes(in);
        this.constructionMethods = readConstructionMethods(in);
        methods = readMethods(in);
        superClass = readSuperClass(in);
        interfaces = readInterfaces(in);
    }
    //xq,用于读取有关于参数和返回值的日志信息
    public void readExternal2(ObjectInput in) throws IOException,
            ClassNotFoundException {
        joinpoint = in.readUTF();
        filename = in.readUTF();
        linenr = in.readInt();
        idHashCode = in.readInt();
        timestamp = in.readLong();
        nanotime = in.readLong();
        eventType = EventType.values()[in.readInt()];
        threadid = in.readLong();
        this.newthreadid=in.readLong();
        className = in.readUTF();
        this.objectID=in.readUTF();

        args=readArguments(in);
        returnData=readReturn(in);
    }
    public void readExternal_FUP(ObjectInput in) throws IOException,
            ClassNotFoundException{
        joinpoint = in.readUTF();
        filename = in.readUTF();
        linenr = in.readInt();
        idHashCode = in.readInt();
        timestamp = in.readLong();
        nanotime = in.readLong();
        eventType = EventType.values()[in.readInt()];
        threadid = in.readLong();
        this.newthreadid=in.readLong();
        className = in.readUTF();
        this.objectID=in.readUTF();
        this.FUP=in.readUTF();
        this.returnType_FUP=in.readUTF();
    }
    private SuperClass[] readInterfaces(ObjectInput in) throws IOException {
        int length = in.readInt();
        if (length > 0){
            SuperClass[] interfaces = new SuperClass[length];
            for (int i = 0; i < length; i++) {
                interfaces[i] = new SuperClass();
                interfaces[i].setClassName(in.readUTF());
                interfaces[i].setAttributes(readAttributes(in));
                interfaces[i].setMethods(readMethods(in));
            }
            return interfaces;
        } else {
            SuperClass[] interfaces = new SuperClass[0];
            return interfaces;
        }
    }

    private SuperClass readSuperClass(ObjectInput in) throws IOException {
        String className = in.readUTF();
        if ("null".equals(className)){
            return null;
        }else {
            SuperClass superClass = new SuperClass();
            superClass.setClassName(className);
            superClass.setAttributes(readAttributes(in));
            superClass.setConstructionMethods(readConstructionMethods(in));
            superClass.setMethods(readMethods(in));
            return superClass;
        }
    }

    private Attribute[] readAttributes(ObjectInput in) throws IOException {
        int numAttributes = in.readInt();
        if (numAttributes > 0) {
            Attribute[] attributes = new Attribute[numAttributes];
            for (int i = 0; i < numAttributes; i++) {
                attributes[i] = new Attribute();
                attributes[i].setModifier(in.readInt());
                attributes[i].setType(in.readUTF());
                attributes[i].setName(in.readUTF());
                //System.out.println("JoinpointInfo:readAttributes:reading:" + attributes[i].getName());
            }
            return attributes;
        } else {
            return new Attribute[0];
        }
    }

    private Method[] readConstructionMethods(ObjectInput in) throws IOException {
        int numConstructionMethods = in.readInt();
        if (numConstructionMethods > 0){
            Method[] constructionMethods = new Method[numConstructionMethods];
            for (int i = 0; i < numConstructionMethods; i++){
                constructionMethods[i] = new Method();
                constructionMethods[i].setModifier(in.readInt());
                constructionMethods[i].setName(in.readUTF());
                int numParam = in.readInt();
                if (numParam > 0){
                    String[] params = new String[numParam];
                    for (int j = 0; j < numParam; j++){
                        params[j] = in.readUTF();
                    }
                    constructionMethods[i].setParamType(params);
                }else{
                    String[] params = new String[0];
                    constructionMethods[i].setParamType(params);
                }
            }
            return constructionMethods;
        } else {
            Method[] constructionMethods = new Method[0];
            return  constructionMethods;
        }
    }

    private Method[] readMethods(ObjectInput in) throws IOException {
        int numMethods = in.readInt();
        if (numMethods > 0){
            Method[] methods = new Method[numMethods];
            for (int i = 0; i < numMethods; i++){
                methods[i] = new Method();
                methods[i].setModifier(in.readInt());
                methods[i].setType(in.readUTF());
                methods[i].setName(in.readUTF());
                int numParam = in.readInt();
                if (numParam > 0){
                    String[] params = new String[numParam];
                    for (int j = 0; j < numParam; j++){
                        params[j] = in.readUTF();
                    }
                    methods[i].setParamType(params);
                }else{
                    String[] params = new String[0];
                    methods[i].setParamType(params);
                }
            }
            return methods;
        } else {
            Method[] methods = new Method[0];
            return methods;
        }
    }

    private Argument[] readArguments(ObjectInput in) throws IOException {
        int numArguments = in.readInt();
        if (numArguments > 0){
            Argument[] arguments = new Argument[numArguments];
            for (int i = 0; i < numArguments; i++){
                arguments[i] = new Argument();
                arguments[i].setType(in.readUTF());
                int count=in.readInt();
                StringBuilder value= new StringBuilder();
                for(int j=0;j<count;j++){
                    value.append(in.readUTF());
                }
                arguments[i].setValue(value.toString());
            }
            return arguments;
        } else {
            Argument[] arguments = new Argument[0];
            return arguments;
        }
    }

    private ReturnData readReturn(ObjectInput in) throws IOException {
        int numReturn = in.readInt();
        if (numReturn > 0){
            ReturnData returnData = new ReturnData();
            returnData.setType(in.readUTF());
            int count=in.readInt();
            StringBuilder value= new StringBuilder();
            for(int j=0;j<count;j++){
                value.append(in.readUTF());
            }
            returnData.setValue(value.toString());
            return returnData;
        } else {
            return new ReturnData();
        }
    }


    @Override
	public String toString() {
		return "JoinpointInfo [joinpoint=" + joinpoint + ", filename="
				+ filename + ", linenr=" + linenr + ", idHashCode="
				+ idHashCode + ", paramTypes=" + Arrays.toString(paramTypes)
				+ ", paramValues=" + Arrays.toString(paramValues)
				+ ", returnType=" + returnType + ", returnValue=" + returnValue
				+ ", timestamp=" + timestamp + ", nanotime=" + nanotime
				+ ", eventType=" + eventType + ", threadid=" + threadid
                + ", className=" + className + "]";
	}
}
