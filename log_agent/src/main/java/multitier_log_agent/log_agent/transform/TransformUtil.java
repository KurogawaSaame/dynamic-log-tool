package multitier_log_agent.log_agent.transform;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CodeConverter;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TransformUtil {

    public static Logger logger = LogManager.getLogger(TransformUtil.class);

    private static boolean lookupMethodSystemExit;
    private static boolean lookupThrowableType;
    
    private static CtMethod methodSystemExit;
    private static CtClass throwableType;

    private static long sysexitIndexCounter = 0;
    
    public static CtMethod getMethodSystemExit() {
        if (methodSystemExit == null && !lookupMethodSystemExit) {
            lookupMethodSystemExit = true;
            try {
                methodSystemExit = ClassPool.getDefault().getMethod(
                        "java.lang.System", "exit");
            } catch (NotFoundException e) {
                methodSystemExit = null;
                logger.error("Could not find System.exit", e);
            }
        }
        return methodSystemExit;
    }
    
    public static CtClass getThrowableType() {
        if (throwableType == null && !lookupThrowableType) {
            lookupThrowableType = true;
            try {
                throwableType = ClassPool.getDefault().get("java.lang.Throwable");
            } catch (NotFoundException e) {
                throwableType = null;
                logger.error("Could not find Throwable class", e);
            }
        }
        return throwableType;
    }
    
    public static boolean isSystemExitProxy(CtBehavior m) {
        return m.getName().startsWith("__System_exit_");
    }
    
    public static void addSystemExitProxy(CtBehavior m, String recordExit) throws CannotCompileException {
        if (methodSystemExit != null) {
            String sysexitName = "__System_exit_" + Long.toString(sysexitIndexCounter);
            String sysexitWrapper = "public static void " + sysexitName + "(int status) {"
                + recordExit
                + "System.exit(status);"
                + "}";
            CtMethod wrapper = CtMethod.make(sysexitWrapper, m.getDeclaringClass());
            m.getDeclaringClass().addMethod(wrapper);
            sysexitIndexCounter++;
    
            CodeConverter convSystemExit = new CodeConverter();
            convSystemExit.redirectMethodCall(methodSystemExit, wrapper);
            m.instrument(convSystemExit);
        }
    }
}
