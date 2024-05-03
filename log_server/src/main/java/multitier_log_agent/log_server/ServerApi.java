package multitier_log_agent.log_server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import multitier_log_agent.log_server.config.ConfigModel;
import multitier_log_agent.log_server.model.IServer;
import multitier_log_agent.log_server.model.ServerStatus;
import multitier_log_agent.log_server.model.StreamWorker;
import multitier_log_agent.log_server.output.AbstractOutput;
import multitier_log_agent.log_server.util.signals.Action1;
import multitier_log_agent.log_server.util.signals.Signal1;
import multitier_log_agent.log_server.util.signals.SignalRegistration;
import multitier_log_agent.log_shared.model.TracePacket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerApi {

    public static Logger logger = LogManager.getLogger(ServerApi.class);

    public final Signal1<ServerStatus> SignalStatus = new Signal1<>();
    
    private ConfigModel config;

    private IServer server;
    private StreamWorker worker;

    private Thread serverThread;
    private Thread workerThread;

    private ServerStatus runStatus;
    private SignalRegistration<Action1<ServerStatus>> sigReg;
    
    public ServerApi(ConfigModel config) {
        this.config = config;
        runStatus = ServerStatus.Offline;
    }
    
    public void start() {
        if (server != null || worker != null
                || serverThread != null || workerThread != null) {
            throw new IllegalStateException("Server already started");
        }
        
        logger.info("Setting up server...");

        BlockingQueue<TracePacket> commQueue = new ArrayBlockingQueue<>(
                config.getCapacityCommBuffer());

        server = config.getServerFactory().newInstance(config.getPort(), commQueue);
        sigReg = server.SignalStatus().register(new Action1<ServerStatus>() {
            @Override
            public void call(ServerStatus t) {
                runStatus = t;
                SignalStatus.dispatch(t);
            }
        });
        
        worker = new StreamWorker(commQueue, config.getCapacityCaseTrack());

        for (AbstractOutput output : config.getOutputs()) {
            output.unregisterAll();
            output.register(worker);
        }
        
        serverThread = new Thread(server);
        workerThread = new Thread(worker);

        logger.info("Start server...");
        workerThread.start();
        serverThread.start();
    }
    
    public void stop() {
        if (server == null || worker == null
                || serverThread == null || workerThread == null) {
            throw new IllegalStateException("Server not started");
        }
        
        logger.info("Shutting down...");
        server.requestStop();
        worker.requestStop();
        
        serverThread.interrupt();
        workerThread.interrupt();
        
        try {
            serverThread.join();
            workerThread.join();
        } catch (InterruptedException e) {
            logger.error("Unexpected interrupt", e);
            e.printStackTrace();
        } finally {
            if (sigReg != null) {
                sigReg.unregister();
            }
            sigReg = null;
            
            for (AbstractOutput output : config.getOutputs()) {
                output.unregisterAll();
            }
            
            server = null;
            worker = null;
            serverThread = null;
            workerThread = null;
            logger.info("Shutdown complete");
        }
    }
    
    public ServerStatus getStatus() {
        return runStatus;
    }
}
