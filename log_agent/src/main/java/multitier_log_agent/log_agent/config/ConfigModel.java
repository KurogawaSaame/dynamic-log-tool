package multitier_log_agent.log_agent.config;

import java.util.ArrayList;
import java.util.List;

import multitier_log_agent.log_agent.classloader.IClassLoaderModifier;
import multitier_log_agent.log_agent.transform.ITransformationRule;

public class ConfigModel {

    public static final String DefaultLogserverHost = "localhost";
    public static final int DefaultLogserverPort = 9000;

    public static final String DefaultIdApp = "Nameless App";
    public static final String DefaultIdTier = "Nameless Tier";
    public static final String DefaultIdNode = "Nameless Node";
    public static final boolean DefaultLogserverTCP = false;
    public static final String DefaultLogserverTCPString = "udp";
    
    private String logserverHost = DefaultLogserverHost;
    private int logserverPort = DefaultLogserverPort;
    private boolean logserverTCP = DefaultLogserverTCP;
    
    private String idApp = DefaultIdApp;
    private String idTier = DefaultIdTier;
    private String idNode = DefaultIdNode;
    
    private List<IClassLoaderModifier> classloaderModifiers = new ArrayList<>();
    
    private List<ITransformationRule> transformationRules = new ArrayList<>();

    public String getLogserverHost() {
        return logserverHost;
    }

    public void setLogserverHost(String logserverHost) {
        this.logserverHost = logserverHost;
    }

    public int getLogserverPort() {
        return logserverPort;
    }

    public void setLogserverPort(int logserverPort) {
        this.logserverPort = logserverPort;
    }

    public String getIdApp() {
        return idApp;
    }

    public void setIdApp(String idApp) {
        this.idApp = idApp;
    }

    public String getIdTier() {
        return idTier;
    }

    public void setIdTier(String idTier) {
        this.idTier = idTier;
    }

    public String getIdNode() {
        return idNode;
    }

    public void setIdNode(String idNode) {
        this.idNode = idNode;
    }

    public List<IClassLoaderModifier> getClassloaderModifiers() {
        return classloaderModifiers;
    }

    public void addClassloaderModifiers(IClassLoaderModifier classloaderModifier) {
        this.classloaderModifiers.add(classloaderModifier);
    }

    public List<ITransformationRule> getTransformationRules() {
        return transformationRules;
    }

    public void addTransformationRules(ITransformationRule transformationRule) {
        this.transformationRules.add(transformationRule);
    }

    public boolean isLogserverTCP() {
        return logserverTCP;
    }

    public void setLogserverTCP(boolean logserverTCP) {
        this.logserverTCP = logserverTCP;
    }
    
}
