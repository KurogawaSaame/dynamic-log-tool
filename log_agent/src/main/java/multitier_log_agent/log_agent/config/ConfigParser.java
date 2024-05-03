package multitier_log_agent.log_agent.config;

import multitier_log_agent.log_agent.classloader.GlassfishWebappClassLoaderModifier;
import multitier_log_agent.log_agent.classloader.IClassLoaderModifier;
import multitier_log_agent.log_agent.transform.ITransformationRule;
import multitier_log_agent.log_agent.transform.rule.InterfacePointcutRule;
import multitier_log_agent.log_agent.transform.rule.ThreadCallPointcutRule;
import multitier_log_agent.log_agent.transform.rule.MethodPointcutRule;
import multitier_log_agent.log_agent.transform.rule.ServletRule;
import multitier_log_agent.log_agent.transform.rule.SocketChannelRule;
import multitier_log_agent.log_agent.transform.rule.SocketRule;
import multitier_log_agent.log_agent.transform.rule.ThreadRule;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigParser {

    public static Logger logger = LogManager.getLogger(ConfigParser.class);

    public static ConfigModel parse(String fileName) {
        XMLConfiguration config;
        try {
            config = new XMLConfiguration(fileName);
        } catch (ConfigurationException e) {
            logger.warn("Could not load configuration file, using default settings");
            config = new XMLConfiguration();
        }
        return parse(config);
    }

    private static ConfigModel parse(XMLConfiguration config) {
        config.setExpressionEngine(new XPathExpressionEngine());
        ConfigModel result = new ConfigModel();
        
        result.setLogserverHost(config.getString("logserver/host", ConfigModel.DefaultLogserverHost));
        result.setLogserverPort(config.getInt("logserver/port", ConfigModel.DefaultLogserverPort));
        result.setLogserverTCP(_parseServerType(
                config.getString("logserver/type", ConfigModel.DefaultLogserverTCPString)));

        result.setIdApp(config.getString("nodeid/application", ConfigModel.DefaultIdApp));
        result.setIdTier(config.getString("nodeid/tier", ConfigModel.DefaultIdTier));
        result.setIdNode(config.getString("nodeid/node", ConfigModel.DefaultIdNode));
        
        for (HierarchicalConfiguration entry : config.configurationsAt("classloader-modifiers/*")) {
            if (entry.getBoolean("enabled", true)) {
                try {
                    IClassLoaderModifier mod = _createClassloaderModifier(entry, result);
                    if (mod != null) {
                        result.addClassloaderModifiers(mod);
                    }
                } catch (IllegalArgumentException e) {
                    logger.error("Illegal output specified", e);
                }
            }
        }

        for (HierarchicalConfiguration entry : config.configurationsAt("transformation-rules/*")) {
            if (entry.getBoolean("enabled", true)) {
                try {
                    ITransformationRule rule = _createTransformationRule(entry, result);
                    if (rule != null) {
                        result.addTransformationRules(rule);
                    }
                } catch (IllegalArgumentException e) {
                    logger.error("Illegal output specified", e);
                }
            }
        }
        
        return result;
    }

    private static boolean _parseServerType(String type) {
        return type.toLowerCase().equals("tcp");
    }

    private static IClassLoaderModifier _createClassloaderModifier(
            HierarchicalConfiguration config, ConfigModel result) {
        switch (config.getRootNode().getName()) {
        case "modifier-glassfish-webapp":
            return new GlassfishWebappClassLoaderModifier();
        }
        throw new IllegalArgumentException("Classloader Modifier not recognized: " + config.getRootNode().getName());
    }

    private static ITransformationRule _createTransformationRule(
            HierarchicalConfiguration config, ConfigModel result) {
        switch (config.getRootNode().getName()) {
        case "method-pointcut":
            return new MethodPointcutRule(config);
        case "interface-pointcut":
            return new InterfacePointcutRule(config);
        case "thread-call-pointcut":
            return new ThreadCallPointcutRule(config);
        case "socket":
            return new SocketRule(config);
        case "socket-channel":
            return new SocketChannelRule(config);
        case "servlet":
            return new ServletRule(config);
        case "threadPool":
        	return new ThreadRule(config);
        }
        throw new IllegalArgumentException("Classloader Modifier not recognized: " + config.getRootNode().getName());
    }
}
