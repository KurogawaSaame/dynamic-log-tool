package multitier_log_agent.log_agent.transform;

import java.util.List;
import java.util.regex.Pattern;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;

public class PointcutUtil {

    public static boolean patternsAccept(List<Pattern> patterns, String subject) {
        for (Pattern pattern : patterns) {
            if (pattern.matcher(subject).matches()) {
                return true;
            }
        }
        return false;
    }

    //是线程相关的调用 todo
    public static boolean patternsAccept(String subject) {
        if (subject.equals("java.lang.Thread.start()")){
            return true;
        }
//        if ("java.util.concurrent.ThreadPoolExecutor.submit()".equals(subject)){
//            return true;
//        }
//        if ("java.util.concurrent.Executor.execute".equals(subject.split("\\(")[0])){
//            return true;
//        }
        return false;
    }
    
    public static boolean hasInterface(CtClass cc, String targetInterfaceName)
            throws NotFoundException {
        // check directly implemented interfaces
        CtClass[] interfaces = cc.getInterfaces();
        for (CtClass ctInterface : interfaces) {
            if (targetInterfaceName.equals(ctInterface.getName())) {
                return true;
            }
        }

        // check if parent class has implemented the interface
        CtClass superCC = cc.getSuperclass();
        if (superCC != null) {
            return hasInterface(superCC, targetInterfaceName);
        }

        return false;
    }
    
    public static boolean hasInterface(CtClass cc, List<Pattern> patterns)
            throws NotFoundException {
        // check directly implemented interfaces
        CtClass[] interfaces = cc.getInterfaces();
        for (CtClass ctInterface : interfaces) {
            for (Pattern pattern : patterns) {
                if (pattern.matcher(ctInterface.getName()).matches()) {
                    return true;
                }
            }
        }

        // check if parent class has implemented the interface
        CtClass superCC = cc.getSuperclass();
        if (superCC != null) {
            return hasInterface(superCC, patterns);
        }

        return false;
    }

    public static boolean hasBody(CtBehavior m) {
        CodeAttribute ca = m.getMethodInfo().getCodeAttribute();
        return ca != null;
    }
    
    public static boolean isAbstract(CtBehavior m) {
        return Modifier.isAbstract(m.getModifiers());
    }
}
