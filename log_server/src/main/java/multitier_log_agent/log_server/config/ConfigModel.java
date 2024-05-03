package multitier_log_agent.log_server.config;

import java.util.ArrayList;
import java.util.List;

import multitier_log_agent.log_server.model.ServerFactory;
import multitier_log_agent.log_server.output.AbstractOutput;

public class ConfigModel {
    
    public static final int DefaultPort = 9000;
    public static final int DefaultCapacityCommBuffer = 100;
    public static final int DefaultCapacityCaseTrack = 100;
    public static final int DefaultCapacityTraceEvents = 1000;
    public static final ServerFactory DefaultServerFactory = new ServerFactory.ServerFactoryUDP();
    public static final String DefaultServerFactoryString = "udp";

    private int port = DefaultPort;
    private int capacityCommBuffer = DefaultCapacityCommBuffer;
    private int capacityCaseTrack = DefaultCapacityCaseTrack;
    private int capacityTraceEvents = DefaultCapacityTraceEvents;
    private List<AbstractOutput> outputs = new ArrayList<AbstractOutput>();
    private ServerFactory serverFactory = DefaultServerFactory;
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public int getCapacityCommBuffer() {
        return capacityCommBuffer;
    }
    
    public void setCapacityCommBuffer(int capacityCommBuffer) {
        this.capacityCommBuffer = capacityCommBuffer;
    }
    
    public int getCapacityCaseTrack() {
        return capacityCaseTrack;
    }
    
    public void setCapacityCaseTrack(int capacityCaseTrack) {
        this.capacityCaseTrack = capacityCaseTrack;
    }
    
    public int getCapacityTraceEvents() {
        return capacityTraceEvents;
    }
    
    public void setCapacityTraceEvents(int capacityTraceEvents) {
        this.capacityTraceEvents = capacityTraceEvents;
    }
    
    public List<AbstractOutput> getOutputs() {
        return outputs;
    }
    
    public void addOutputs(AbstractOutput outputs) {
        this.outputs.add(outputs);
    }

    public ServerFactory getServerFactory() {
        return serverFactory;
    }

    public void setServerFactory(ServerFactory serverFactory) {
        this.serverFactory = serverFactory;
    }
}
