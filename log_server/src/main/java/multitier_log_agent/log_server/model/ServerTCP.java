package multitier_log_agent.log_server.model;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import multitier_log_agent.log_server.util.signals.Signal1;
import multitier_log_agent.log_shared.model.TracePacket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerTCP implements IServer {

    public static Logger logger = LogManager.getLogger(ServerTCP.class);

    public final Signal1<ServerStatus> SignalStatus = new Signal1<>();
    
    /**
     * Server
     */
    private int serverPort;
    private ServerSocket serverSocket;

    /**
     * Threading
     */
    public boolean doRun = true;
    private BlockingQueue<TracePacket> commQueue;

    private List<Thread> connThreads = new ArrayList<Thread>();
    
    public ServerTCP(int serverPort, BlockingQueue<TracePacket> commQueue) {
        this.serverPort = serverPort;
        this.commQueue = commQueue;
    }

    @Override
    public void run() {
        SignalStatus.dispatch(ServerStatus.Starting);
        try {
            // setup server receive
            serverSocket = new ServerSocket(this.serverPort);
            logger.info("TCP Server is listening on port "
                    + Integer.toString(this.serverPort));

            // receive connections
            SignalStatus.dispatch(ServerStatus.Running);
            while (doRun && !Thread.interrupted()) {
                final Socket clientSocket = serverSocket.accept();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ObjectInputStream ois;
                        try {
                            ois = new ObjectInputStream(clientSocket.getInputStream());
                            while (doRun && !Thread.interrupted()) {
                                try {
                                    TracePacket packet = new TracePacket();
                                    packet.readExternal(ois);
                                    commQueue.put(packet);
                                } catch (EOFException e) {
                                    // end of socket -> client closed
                                    break;
                                } catch (SocketException e) {
                                    if (clientSocket.isClosed() || !clientSocket.isConnected()
                                            || e.getMessage().equals("Connection reset")) {
                                        break;
                                    } else {
                                        logger.error("Socket Error in client connection", e);
                                    }
                                } catch (IOException e) {
                                    if (!doRun) {
                                        // App.logger.info("Server stopped accepting client connections");
                                        break;
                                    } else {
                                        logger.error("IO Error in client connection", e);
                                    }
                                } catch (ClassNotFoundException e) {
                                    logger.error("Error decoding trace packet", e);
                                }
                            }
                        } catch (IOException e) {
                            logger.error("IO Error (during setup)", e);
                        } catch (InterruptedException e) {
                            SignalStatus.dispatch(ServerStatus.Error);
                            logger.error("Interrupt Error accepting client connection", e);
                        } finally {
                            if (!clientSocket.isClosed()) {
                                try {
                                    clientSocket.close();
                                } catch (IOException e) {
                                    logger.error("Interrupt IO during client shutdown", e);
                                }
                            }
                        }
                    }
                });
                thread.start();
                logger.info("New client connection accepted "
                        + clientSocket.getRemoteSocketAddress().toString());
                connThreads.add(thread);
            }
        } catch (IOException e) {
            if (serverSocket != null && !serverSocket.isClosed()) {
                SignalStatus.dispatch(ServerStatus.Error);
                logger.error("IO Error accepting client connection", e);
            }else{
            	logger.error("can not setup server", e);
            }
        } finally {
            SignalStatus.dispatch(ServerStatus.Shutdown);
            // close socket
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    logger.error("Interrupt IO during server shutdown", e);
                }
            }
            serverSocket = null;
            SignalStatus.dispatch(ServerStatus.Offline);
        }
    }

    @Override
    public void requestStop() {
        doRun = false;
        for (Thread thread : connThreads) {
            thread.interrupt();
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                logger.error("Interrupt IO during server shutdown", e);
            }
        }
        serverSocket = null;
    }

    @Override
    public Signal1<ServerStatus> SignalStatus() {
        return SignalStatus;
    }
}
