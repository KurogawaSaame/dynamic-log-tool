package multitier_log_agent.log_agent;

import java.lang.instrument.Instrumentation;

import javassist.CannotCompileException;
import multitier_log_agent.log_agent.config.ConfigModel;
import multitier_log_agent.log_agent.config.ConfigParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Agent {

    public static Logger logger = LogManager.getLogger(Agent.class);

//    public static void agentmain(String agentArgs, Instrumentation inst){
//        System.out.println("开始插装！入参：" + agentArgs);
//        String configFile = "agent-config.xml";
//        try {
//
//            // Parse configuration
//            logger.info("Parsing configurations");
//            ConfigModel config = ConfigParser.parse(configFile);
//
//            // create transformer
//            logger.info("Configuring logger client code transformer");
//            ClientTransformer t = new ClientTransformer(inst, config);
//
//            // Use transformer
//            logger.info("Attaching logger client code transformer");
//            inst.addTransformer(t,true);
//            //inst.retransformClasses(java.util.concurrent.ThreadPoolExecutor.class);
//            Class[] classes = inst.getAllLoadedClasses();
//            for(Class cls :classes){
//                if (needReload(cls, agentArgs)){
//                    //System.out.println("已加载的成员类有哪些"+ cls.getName());
//                    try {
//                        inst.retransformClasses(cls);
//                    } catch (UnmodifiableClassException e) {
//                        // TODO 自动生成的 catch 块
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//        } catch (CannotCompileException e) {
//            logger.error("Compilation error", e);
//        }
//    }

    public static void premain(String agentArgs, Instrumentation inst) {
        String configFile = "./agent-config.xml";
        try {
            if (agentArgs != null && !agentArgs.isEmpty()) {
                configFile = agentArgs;
            }

            // Parse configuration
            logger.info("Parsing configurations");
            ConfigModel config = ConfigParser.parse(configFile);

            // create transformer
            logger.info("Configuring logger client code transformer");
            ClientTransformer t = new ClientTransformer(inst, config);

            // Use transformer
            logger.info("Attaching logger client code transformer");
            inst.addTransformer(t);

        } catch (CannotCompileException e) {
            logger.error("Compilation error", e);
        }
    }

    public static void main(String args[]){
//    	String configFile = "C:\\Users\\admin\\Desktop\\experiment\\0522parentThreadId\\agent-config4call.xml";
//    	ConfigModel config = ConfigParser.parse(configFile);
//    	String longName="java.lang.Thread(java.lang.Runnable)";
//    	List<Pattern> includeCalls=null;
//    	for(ITransformationRule rule:config.getTransformationRules())
//    	if (rule instanceof MethodCallPointcutRule) {
//            includeCalls = ((MethodCallPointcutRule) rule).getIncludeCalls();
//        }
//    	System.out.println(longName.startsWith(LogClient.class.getName())+"------"+PointcutUtil.patternsAccept(includeCalls, longName));
//    	if (!longName.startsWith(LogClient.class.getName())
//                && PointcutUtil.patternsAccept(includeCalls, longName)) {
//                logger.debug("\tInstrument on expr '" + longName + "'");
//    	}
    	Thread t=new Thread();long id=t.getId();
        System.out.print(t instanceof java.lang.Thread);
    }

    private static boolean needReload(Class cls, String agentArgs){
        if (cls.getName().startsWith(agentArgs) && !cls.getName().contains("Lambda")){
            return true;
        }
        if (cls.getName().startsWith("java.util.concurrent.ThreadPoolExecutor") ||
                cls.getName().startsWith("java.util.concurrent.AbstractExecutorService")){
            return true;
        }
//        if (cls.getName().startsWith("java.net.SocketInputStream") ||
//                cls.getName().startsWith("java.net.SocketOutputStream")){
//            return true;
//        }
        if (cls.getName().startsWith("sun.nio.ch.SocketChannelImpl")){
            return true;
        }
        if (hasServlet(cls, "javax.servlet.Servlet")){
            return true;
        }
        return false;
    }

    private static boolean hasServlet(Class cls, String targetInterfaceName){
        // check directly implemented interfaces
        Class[] interfaces = cls.getInterfaces();
        for (Class ctInterface : interfaces) {
            if (targetInterfaceName.equals(ctInterface.getName())) {
                return true;
            }
        }

        // check if parent class has implemented the interface
        Class superCC = cls.getSuperclass();
        if (superCC != null) {
            return hasServlet(superCC, targetInterfaceName);
        }
        return false;
    }
}
