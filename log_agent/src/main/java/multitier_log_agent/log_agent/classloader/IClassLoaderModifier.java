package multitier_log_agent.log_agent.classloader;

import javassist.CtClass;

public interface IClassLoaderModifier {

    String getName();
    
    void preTransform(ClassLoader loader, CtClass cc);
    
    void postTransform(ClassLoader loader, CtClass cc);

    
}
