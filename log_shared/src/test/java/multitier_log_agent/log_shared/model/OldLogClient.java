package multitier_log_agent.log_shared.model;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class OldLogClient {

    private static boolean isInit = false;

    private static String idPrefix;
    private static int baseSBSize = 16;

    private static Charset charset = StandardCharsets.US_ASCII;

    public static final String SEP = "$";

//    private static final String LIFECYCLE_START = "start";
    private static final String LIFECYCLE_COMPLETE = "complete";

    /**
     * Make value save for transport
     * 
     * @param value
     * @return
     */
    public static String makeSave(String value) {
        return value.replace(SEP, "-");
    }

    public static String makeSave(String value, String replace) {
        return value.replace(SEP, replace);
    }
    
    /**
     * Setup client logger for use based on configured settings
     * 
     * @param logserverHost
     * @param logserverPort
     * @param idApp
     * @param idTier
     * @param idNode
     */
    public static void setup(String idApp, String idTier, String idNode) {
       
            idPrefix = idApp + SEP + idTier + SEP + idNode;
            baseSBSize = SEP.length() * 5 + idPrefix.length()
                    + LIFECYCLE_COMPLETE.length()
                    + Long.toString(System.currentTimeMillis()).length()
                    + Long.toString(Thread.currentThread().getId()).length()
                    + 3 * OldLogClient.class.getName().length();
            // 3 * LogClient.class.getName().length() , reasoning:
            // 1.5 for name length and 1.5 for regionArray length estimation
        
    }

    /**
     * Initialize client UDP socket
     */
    private static void doInit() {
        if (!isInit) {
                isInit = true;
        }
    }

    // ---- Sockets ----

//    public static void recordSocketStart(final Socket socket, final String name,
//            final long currentTime, final long threadId) {
//        recordSocketEvent(socket, name, LIFECYCLE_START, currentTime, threadId);
//    }
//
//    public static void recordSocketComplete(final Socket socket, final String name,
//            final long currentTime, final long threadId) {
//        recordSocketEvent(socket, name, LIFECYCLE_COMPLETE, currentTime, threadId);
//    }
//
//    public static void recordSocketEvent(final Socket socket, final String name,
//            final String lifecycle, final long currentTime, final long threadId) {
//
//        final String localHost = socket.getLocalAddress().getHostAddress();
//        final String localPort = Integer.toString(socket.getLocalPort());
//
//        final String remoteHost = socket.getInetAddress().getHostAddress();
//        final String remotePort = Integer.toString(socket.getPort());
//
//        final StringBuilder entry = new StringBuilder(localHost.length()
//                + localPort.length() + remoteHost.length()
//                + remotePort.length() + 3); // plus ":", plus ","
//
//        entry.append(localHost);
//        entry.append(":");
//        entry.append(localPort);
//        entry.append(",");
//        entry.append(remoteHost);
//        entry.append(":");
//        entry.append(remotePort);
//        recordEvent(name, lifecycle, currentTime, threadId,
//                entry.toString());
//    }
//
//    // ---- Socket Channels ----
//
//    public static void recordSocketChannelStart(final SocketChannel channel, final String name,
//            final long currentTime, final long threadId) {
//        recordSocketChannelEvent(channel, name, LIFECYCLE_START, currentTime, threadId);
//    }
//
//    public static void recordSocketChannelComplete(final SocketChannel channel, final String name,
//            final long currentTime, final long threadId) {
//        recordSocketChannelEvent(channel, name, LIFECYCLE_COMPLETE, currentTime, threadId);
//    }
//
//    public static void recordSocketChannelEvent(final SocketChannel channel, final String name,
//            final String lifecycle, final long currentTime, final long threadId) {
//
//        Socket socket = channel.socket();
//        final String localHost = socket.getLocalAddress().getHostAddress();
//        final String localPort = Integer.toString(socket.getLocalPort());
//
//        final String remoteHost = socket.getInetAddress().getHostAddress();
//        final String remotePort = Integer.toString(socket.getPort());
//
//        final StringBuilder entry = new StringBuilder(localHost.length()
//                + localPort.length() + remoteHost.length()
//                + remotePort.length() + 3); // plus ":", plus ","
//
//        entry.append(localHost);
//        entry.append(":");
//        entry.append(localPort);
//        entry.append(",");
//        entry.append(remoteHost);
//        entry.append(":");
//        entry.append(remotePort);
//        recordEvent(name, lifecycle, currentTime, threadId,
//                entry.toString());
//    }
//    
//    // ---- Servlets ----
//
//    public static void recordServletStart(final String localHost,
//            final String localPort, final String remoteHost,
//            final String remotePort, final long currentTime, final long threadId) {
//        recordServletEvent(localHost, localPort, remoteHost, remotePort,
//                LIFECYCLE_START, currentTime, threadId);
//    }
//
//    public static void recordServletComplete(final String localHost,
//            final String localPort, final String remoteHost,
//            final String remotePort, final long currentTime, final long threadId) {
//        recordServletEvent(localHost, localPort, remoteHost, remotePort,
//                LIFECYCLE_COMPLETE, currentTime, threadId);
//    }
//
//    public static void recordServletEvent(final String localHost,
//            final String localPort, final String remoteHost,
//            final String remotePort, final String lifecycle,
//            final long currentTime, final long threadId) {
//
//        final StringBuilder entry = new StringBuilder(localHost.length()
//                + localPort.length() + remoteHost.length()
//                + remotePort.length() + 3); // plus prefix, plus ":", plus " - "
//
//        entry.append(localHost);
//        entry.append(":");
//        entry.append(localPort);
//        entry.append(",");
//        entry.append(remoteHost);
//        entry.append(":");
//        entry.append(remotePort);
//        recordEvent("javax.servlet.Servlet", lifecycle, currentTime, threadId,
//                entry.toString());
//
//    }
//
//    // ---- Joinpoints ----
//
//    /**
//     * Record start event for a traced method
//     * 
//     * @param name
//     * @param currentTime
//     * @param threadId
//     * @param regionArray
//     */
//    public static void recordEntry(final String name, final long currentTime,
//            final long threadId, final String regionArray) {
//        recordEvent(name, LIFECYCLE_START, currentTime, threadId, regionArray);
//    }
//
//    /**
//     * Record complete event for a traced method
//     * 
//     * @param name
//     * @param currentTime
//     * @param threadId
//     * @param regionArray
//     */
//    public static void recordExit(final String name, final long currentTime,
//            final long threadId, final String regionArray) {
//        recordEvent(name, LIFECYCLE_COMPLETE, currentTime, threadId,
//                regionArray);
//    }

    /**
     * Record a event for a traced method
     * 
     * @param name
     * @param currentTime
     * @param threadId
     * @param regionArray
     */
    public static byte[] recordEvent(final String name, final String lifecycle,
            final long currentTime, final long threadId,
            final String regionArray) {
        doInit();
        if (isInit) {
            final java.lang.StringBuilder entry = new java.lang.StringBuilder(
                    baseSBSize);
            entry.append(idPrefix);
            entry.append(SEP);
            entry.append(name);
            entry.append(SEP);
            entry.append(lifecycle);
            entry.append(SEP);
            entry.append(currentTime);
            entry.append(SEP);
            entry.append(threadId);
            entry.append(SEP);
            entry.append(regionArray);

                return entry.toString().getBytes(charset);
        }
        return null;
    }
}
