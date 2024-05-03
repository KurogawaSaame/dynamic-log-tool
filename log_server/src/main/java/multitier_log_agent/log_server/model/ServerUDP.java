package multitier_log_agent.log_server.model;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;

import multitier_log_agent.log_server.util.signals.Signal1;
import multitier_log_agent.log_shared.model.TracePacket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerUDP implements IServer {

    public static Logger logger = LogManager.getLogger(ServerUDP.class);

    public final Signal1<ServerStatus> SignalStatus = new Signal1<>();
    
    /**
     * Server
     */
    private int serverPort;
    private DatagramSocket serverSocket;

    /**
     * Threading
     */
    public boolean doRun = true;
    private BlockingQueue<TracePacket> commQueue;

    public ServerUDP(int serverPort, BlockingQueue<TracePacket> commQueue) {
        this.serverPort = serverPort;
        this.commQueue = commQueue;
    }

    @Override
    public void run() {
        SignalStatus.dispatch(ServerStatus.Starting);
        try {
            // setup server receive
            serverSocket = new DatagramSocket(this.serverPort);
            byte[] data = new byte[2048];
            DatagramPacket dgp = new DatagramPacket(data, data.length);
            logger.info("UDP Server is listening on port "
                    + Integer.toString(this.serverPort));

            // receive packets
            SignalStatus.dispatch(ServerStatus.Running);
            while (doRun && !Thread.interrupted()) {
                try {
                    serverSocket.receive(dgp);
                    TracePacket packet = new TracePacket();
                    packet.fromByteArray(data);
                    commQueue.put(packet);
                } catch (IOException e) {
                    if (!doRun) {
                        // App.logger.info("Server stopped accepting client connections");
                        break;
                    } else {
                        logger.error("Error accepting client connection", e);
                    }
                } catch (ClassNotFoundException e) {
                    logger.error("Error decoding trace packet", e);
                }
            }

        } catch (IOException e) {
            SignalStatus.dispatch(ServerStatus.Error);
            logger.error("IO Error accepting client connection", e);
        } catch (InterruptedException e) {
            SignalStatus.dispatch(ServerStatus.Error);
            logger.error("Interrupt Error accepting client connection", e);
        } finally {
            SignalStatus.dispatch(ServerStatus.Shutdown);
            // close socket
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            serverSocket = null;
            SignalStatus.dispatch(ServerStatus.Offline);
        }
    }

    @Override
    public void requestStop() {
        doRun = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        serverSocket= null;
    }

    @Override
    public Signal1<ServerStatus> SignalStatus() {
        return SignalStatus;
    }
}
