package multitier_log_agent.log_server.config;

import java.io.IOException;

import multitier_log_agent.log_server.model.ServerFactory;
import multitier_log_agent.log_server.output.AbstractOutput;
import multitier_log_agent.log_server.output.OutputLogfile;

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


        result.setPort(config.getInt("server/port", ConfigModel.DefaultPort));
        result.setCapacityCommBuffer(
            config.getInt("internal/capacity/comm-buffer",
                    ConfigModel.DefaultCapacityCommBuffer));
        result.setCapacityCaseTrack(
                config.getInt("internal/capacity/case-track",
                    ConfigModel.DefaultCapacityCaseTrack));
        result.setCapacityTraceEvents(
                config.getInt("internal/capacity/trace-events",
                    ConfigModel.DefaultCapacityTraceEvents));
        result.setServerFactory(_parseServerType(
                config.getString("server/type",
                        ConfigModel.DefaultServerFactoryString)));

        for (HierarchicalConfiguration outputEntry : config.configurationsAt("output/*")) {
            if (outputEntry.getBoolean("enabled", true)) {
                try {
                    AbstractOutput output = _createOutput(outputEntry, result);
                    if (output != null) {
                        result.addOutputs(output);
                    }
                } catch (IllegalArgumentException e) {
                    logger.error("Illegal output specified", e);
                } catch (IOException e) {
                    logger.error("Could not setup output", e);
                }
            }
        }

        return result;
    }

    private static ServerFactory _parseServerType(String type) {
        switch (type.toLowerCase()) {
        case "tcp":
            return new ServerFactory.ServerFactoryTCP();
        case "udp":
        default:
            return new ServerFactory.ServerFactoryUDP();
        }
    }

    private static AbstractOutput _createOutput(
            HierarchicalConfiguration config, ConfigModel result) throws IOException {
        switch (config.getRootNode().getName().toLowerCase()) {
        case "logfile":
            return new OutputLogfile(config);
        /*case "xesfile-buffered":
            return new OutputXesfileBuffered(config, result.getCapacityTraceEvents());
        case "xesfile-stream":
        case "xesfile":
            return new OutputXesfileStream(config);*/
        }
        throw new IllegalArgumentException("Output not recognized: " + config.getRootNode().getName());
    }
}
