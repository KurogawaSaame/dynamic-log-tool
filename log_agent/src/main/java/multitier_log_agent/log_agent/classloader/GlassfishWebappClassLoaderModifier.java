package multitier_log_agent.log_agent.classloader;

import java.lang.reflect.Field;

import javassist.CtClass;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GlassfishWebappClassLoaderModifier implements IClassLoaderModifier {

  public static Logger logger = LogManager.getLogger(GlassfishWebappClassLoaderModifier.class);
    
    Field fieldStarted;
    boolean valueStarted;

    public String getName() {
	return "Glassfish Webapp ClassLoader Modifier";
    }
    
    public void preTransform(ClassLoader loader, CtClass cc) {
	loader = cc.getClassPool().getClassLoader();
	if (loader != null && loader.getClass().getName().equals("org.glassfish.web.loader.WebappClassLoader")) {
	    logger.debug("Detected WebappClassLoader");
	    try {
		fieldStarted = loader.getClass().getDeclaredField("started");
		fieldStarted.setAccessible(true);
		valueStarted = fieldStarted.getBoolean(loader);
		fieldStarted.setBoolean(loader, true);
		logger.debug("WebappClassLoader Modifier applied");
	    } catch (Exception e) {
		logger.error("Could not access setting WebappClassLoader started", e);
	    }
	}
    }

    public void postTransform(ClassLoader loader, CtClass cc) {
	loader = cc.getClassPool().getClassLoader();
	if (loader != null && loader.getClass().getName().equals("org.glassfish.web.loader.WebappClassLoader")) {
	    try {
		fieldStarted.setBoolean(loader, valueStarted);
		logger.debug("WebappClassLoader Modifier reverted");
	    } catch (Exception e) {
		logger.error("Could not revert setting WebappClassLoader started", e);
	    }
	}
    }

}
