package multitier_log_agent.log_agent.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import multitier_log_agent.log_shared.model.CallInfo;
import multitier_log_agent.log_shared.model.CommInfo;
import multitier_log_agent.log_shared.model.ExceptionInfo;
import multitier_log_agent.log_shared.model.JoinpointInfo;
import multitier_log_agent.log_shared.model.NodeInfo;
import multitier_log_agent.log_shared.model.ThreadInfo;
import multitier_log_agent.log_shared.model.TracePacket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogClient {

    public static Logger logger = LogManager.getLogger(LogClient.class);

    private static boolean isInit = false;

    private static boolean useTCP = true;

    private static InetAddress ia = null;
    private static int serverPort = 9000;

    private static DatagramSocket socketUDP = null;

    private static Socket socketTCP = null;
    private static ObjectOutputStream socketOut = null;

    private static NodeInfo nodeInfo = null;

    /**
     * Setup client logger for use based on configured settings
     *
     * @param logserverHost
     * @param logserverPort
     * @param idApp
     * @param idTier
     * @param idNode
     */
    public static void setup(final String logserverHost,
            final int logserverPort, boolean useTCP,
            String idApp, String idTier, String idNode) {
        try {
            // setup connection
            ia = InetAddress.getByName(logserverHost);
            serverPort = logserverPort;
            nodeInfo = new NodeInfo(idApp, idTier, idNode);
            LogClient.useTCP = useTCP;

            // initial setup, ensure clean case
            recordEvent(TracePacket.createNewCase(nodeInfo));
        } catch (UnknownHostException e) {
            LogClient.logger.error("Could not connect to log server ("
                    + logserverHost + ")", e);
        }
    }

    /**
     * Initialize client UDP socket
     */
    private static void doInit() {
        if (!isInit) {
            try {
                if (useTCP) {
                    // TCP
                    socketTCP = new Socket(ia, serverPort);
                    socketOut = new ObjectOutputStream(socketTCP.getOutputStream());
                } else {
                    // UDP
                    socketUDP = new DatagramSocket();
                }
                isInit = true;
            } catch (SocketException e) {
                LogClient.logger.error("Could not create logger socket", e);
            } catch (IOException e) {
                LogClient.logger.error("Could not create logger socket", e);
            }
        }
    }

    public static int bool2int(boolean bool) {
        return bool ? 1 : 0;
    }

    public static void recordJoinpoint(JoinpointInfo joinpointInfo,String pattern, String[] regions) {
        recordEvent(TracePacket.createJoinpoint(nodeInfo, joinpointInfo, pattern,regions));
    }
    public static TracePacket recordJoinpoint_Test(JoinpointInfo joinpointInfo, String pattern,String[] regions) {
        return TracePacket.createJoinpoint(nodeInfo, joinpointInfo, pattern,regions);
    }

    public static void recordCommJoinpoint(JoinpointInfo joinpointInfo, CommInfo commInfo, String[] regions) {
        recordEvent(TracePacket.createCommJoinpoint(nodeInfo, joinpointInfo, commInfo, regions));
    }

    public static void recordCallJoinpoint(JoinpointInfo joinpointInfo, CallInfo callInfo, String[] regions) {
        recordEvent(TracePacket.createCallJoinpoint(nodeInfo, joinpointInfo, callInfo, regions));
    }

    public static void recordExceptionJoinpoint(JoinpointInfo joinpointInfo, ExceptionInfo exceptionInfo, String[] regions) {
        recordEvent(TracePacket.createExceptionJoinpoint(nodeInfo, joinpointInfo, exceptionInfo, regions));
    }

    public static void recordThreadJoinpoint(JoinpointInfo joinpointInfo,ThreadInfo threadInfo){
    	recordEvent(TracePacket.createThreadJoinpoint(nodeInfo, joinpointInfo, threadInfo));
    }

    /*
     * Record a event for a traced method
     *
     * @param name
     * @param currentTime
     * @param threadId
     * @param regionArray
     */
    private static void recordEvent(TracePacket packet) {
        doInit();
        if (isInit) {
            try {
                if (useTCP) {
                    // TCP
                    synchronized (socketOut) {
                        packet.writeExternal(socketOut);
                        socketOut.flush();
                    }
                } else {
                    // UDP
                    byte[] bytes = packet.toByteArray();
                    java.net.DatagramPacket dgp = new java.net.DatagramPacket(
                            bytes, bytes.length, ia, serverPort);
                    socketUDP.send(dgp);
                }
            } catch (IOException e) {
                LogClient.logger.error("Could not send logger packet", e);
            }
        }
    }

}
