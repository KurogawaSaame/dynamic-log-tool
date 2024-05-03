package multitier_log_agent.log_agent;

import gnu.trove.set.hash.THashSet;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.*;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import multitier_log_agent.log_agent.classloader.IClassLoaderModifier;
import multitier_log_agent.log_agent.client.LogClient;
import multitier_log_agent.log_agent.config.ConfigModel;
import multitier_log_agent.log_agent.transform.ITransformationRule;

import multitier_log_agent.log_agent.classlist.ClassesNames;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ClientTransformer implements ClassFileTransformer {

    public static Logger logger = LogManager.getLogger(Agent.class);

    private List<ITransformationRule> transformRules = new ArrayList<>();
    private int numRules = 0;
    private ClassPool classPool = ClassPool.getDefault();
    private Set<ClassLoader> addedLoaders = new THashSet<ClassLoader>();

    private List<IClassLoaderModifier> classLoaderModifiers = new ArrayList<IClassLoaderModifier>();

    public ClientTransformer(Instrumentation inst, ConfigModel config) throws CannotCompileException {
        createClientClass(inst, config);

        for (IClassLoaderModifier mod : config.getClassloaderModifiers()) {
            addClassLoaderModifier(mod);
        }

        for (ITransformationRule rule : config.getTransformationRules()) {
            addRule(rule);
        }

        // sort rules according to their internally defined order,
        // this ensure the instrumentation happens in the right order
        // (prevents null pointers due to prematurely changed code stack)
        Collections.sort(transformRules);
    }

    /*
     * Setup logger client class
     *
     * @param logserverHost
     * @param logserverPort
     * @param idApp
     * @param idTier
     * @param idNode
     * @throws CannotCompileException
     */
    public void createClientClass(Instrumentation inst, ConfigModel config)
            throws CannotCompileException {

        String logserverHost = config.getLogserverHost();
        int logserverPort = config.getLogserverPort();
        boolean logserverTCP = config.isLogserverTCP();
        String idApp = config.getIdApp();
        String idTier = config.getIdTier();
        String idNode = config.getIdNode();

        LogClient.setup(logserverHost, logserverPort, logserverTCP,
                idApp, idTier, idNode);
        // Note: see the manifest setup in pom.xml
        // Via <Boot-Class-Path>....jar</Boot-Class-Path>
        // The LogClient class is findable via the root class loader

        // Add log client class loader to head of class pool
        // Fixes issue with classloader exceptions in glassfish environment
        logger.debug("Prepare ClassPool Default");
        classPool.insertClassPath(new ClassClassPath(LogClient.class));
        //classPool.insertClassPath(new LoaderClassPath(LogClient.class.getClassLoader()));
        logger.debug("Successfully prepared ClassPool Default");

        logger.debug("Prepare ClassLoader Default");
        ClassLoader classLoader = classPool.getClassLoader();
        try {
            classLoader.loadClass(LogClient.class.getName());
        } catch (ClassNotFoundException e) {
            logger.error("Could not preload log client in Default ClassLoader", e);
        }
        logger.debug("Successfully prepared ClassLoader Default");

        logger.info("Setup client logger targeting: " + logserverHost
                + ":" + Integer.toString(logserverPort)
                + " via " + (logserverTCP ? "TCP" : "UDP"));
    }

    /**
     * Add transformation rule
     *
     * @param rule
     */
    public void addRule(ITransformationRule rule) {
        transformRules.add(rule);
        numRules = transformRules.size();

        logger.info("Registered Transformation rule: " + rule.getName());
    }

    public void addClassLoaderModifier(IClassLoaderModifier modifier) {
        classLoaderModifiers.add(modifier);

        logger.info("Registered Class Loader modifier: " + modifier.getName());
    }

    /**
     * ClassFileTransformer implementation using registered rules
     */
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer)  {

//        for (ITransformationRule rule : transformRules) {
//            System.out.println(rule.getName());
//        }
//        return null;

        //System.out.println("进入transform");
        //System.out.println("********************" + dotClassName + "********************");
//        if (dotClassName.startsWith("java.lang.")) {
//            System.out.println("---------------------------------------");
//        }
        // transforming classes in the java.lang package has been known
        // to cause trouble


        String dotClassName = className.replace('/', '.');
        CtClass cc = null;
        if (!dotClassName.startsWith("java.lang.")) {
            boolean appliedModifiers = false;
            try {
                // add loader class path to look into dependency jars
                if (!addedLoaders.contains(loader)) {
                    classPool.appendClassPath(new LoaderClassPath(loader));
                }

                // prep class for transformation
                ByteArrayInputStream ccStream = new ByteArrayInputStream(
                        classfileBuffer);
                cc = classPool.makeClass(ccStream);


                // prep loaders
                for (IClassLoaderModifier modifier : classLoaderModifiers) {
                    modifier.preTransform(loader, cc);
                }
                appliedModifiers = true;


                boolean transformResult = false;
                for (ITransformationRule rule : transformRules) {

                    try {

                        boolean result = rule.transform(cc); // can throw exception
                        transformResult = transformResult || result;
                        if (result) {
//                            logger.debug("Transformed class '" + dotClassName
//                                    + "' with rule '" + rule.getName() + "'");
                        }
                    } catch (Exception e) {
                        logger.error("Exception in rule '" + rule.getName() + "'", e);
                    }
                }

                // revert loaders
                for (IClassLoaderModifier modifier : classLoaderModifiers) {
                    modifier.postTransform(loader, cc);
                }
                appliedModifiers = false;

                // modify class based on rule
                if (transformResult) {
                    byte[] byteCode = cc.toBytecode();
                    cc.detach();
                    cc = null;
                    return byteCode;
                }

            } catch (Exception e) {
                logger.error("Exception during transformation", e);
                if (appliedModifiers) {
                    for (IClassLoaderModifier modifier : classLoaderModifiers) {
                        modifier.postTransform(loader, null);
                    }
                }
            } finally {
                if (cc != null) {
                    cc.detach();
                }
            }
        }
        return null;
    }


}
