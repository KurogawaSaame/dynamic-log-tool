package multitier_log_agent.log_shared.model;
import java.io.IOException;
import java.util.UUID;

import junit.framework.TestCase;

public class ExternalizeTest extends TestCase {

    public void testNodeInfo() throws ClassNotFoundException, IOException {
        UUID uuid = UUID.randomUUID();
        String idApp = "App X";
        String idTier = "Main";
        String idNode = "Node 1";

        NodeInfo nodeInfo = new NodeInfo(uuid, idApp, idTier, idNode);
        assertEquals(uuid, nodeInfo.getNodeSessionId());
        assertEquals(idApp, nodeInfo.getApp());
        assertEquals(idTier, nodeInfo.getTier());
        assertEquals(idNode, nodeInfo.getNode());

        byte[] data = Externalize.toByteArray(nodeInfo);
        assertNotNull(data);
        assertTrue(data.length > 0);

        NodeInfo newInfo = new NodeInfo();
        assertNull(newInfo.getNodeSessionId());
        assertNull(newInfo.getApp());
        assertNull(newInfo.getTier());
        assertNull(newInfo.getNode());

        Externalize.fromByteArray(data, newInfo);
        assertEquals(uuid, newInfo.getNodeSessionId());
        assertEquals(idApp, newInfo.getApp());
        assertEquals(idTier, newInfo.getTier());
        assertEquals(idNode, newInfo.getNode());
    }

    public void testCommInfo() throws ClassNotFoundException, IOException {
        String localHost = "127.0.0.1";
        int localPort = 9000;
        String remoteHost = "127.20.21.22";
        int remotePort = 9012;

        CommInfo commInfo = new CommInfo(localHost, localPort, remoteHost,
                remotePort);
        assertEquals(localHost, commInfo.getLocalHost());
        assertEquals(localPort, commInfo.getLocalPort());
        assertEquals(remoteHost, commInfo.getRemoteHost());
        assertEquals(remotePort, commInfo.getRemotePort());

        byte[] data = Externalize.toByteArray(commInfo);
        assertNotNull(data);
        assertTrue(data.length > 0);

        CommInfo newInfo = new CommInfo();
        assertNull(newInfo.getLocalHost());
        assertTrue(newInfo.getLocalPort() == 0);
        assertNull(newInfo.getRemoteHost());
        assertTrue(newInfo.getRemotePort() == 0);

        Externalize.fromByteArray(data, newInfo);
        assertEquals(localHost, newInfo.getLocalHost());
        assertEquals(localPort, newInfo.getLocalPort());
        assertEquals(remoteHost, newInfo.getRemoteHost());
        assertEquals(remotePort, newInfo.getRemotePort());
    }

    public void testTelemetryInfo() throws ClassNotFoundException, IOException {

        TelemetryInfo telInfo = new TelemetryInfo();
        assertTrue(0 == telInfo.getProcessCpuLoad());
        assertTrue(0 == telInfo.getProcessCpuTime());
        assertTrue(0 == telInfo.getSystemCpuLoad());
        assertTrue(0 == telInfo.getFreeMemory());
        assertTrue(0 == telInfo.getMaxMemory());
        assertTrue(0 == telInfo.getTotalMemory());

        for (int i = 0; i < 100000000; i++) {
            double j = Math.pow(i, 10);
        }
        telInfo.captureTelemetry();
        assertTrue(0 != telInfo.getProcessCpuLoad());
        assertTrue(0 != telInfo.getProcessCpuTime());
//        assertTrue(0 != telInfo.getSystemCpuLoad());
        assertTrue(0 != telInfo.getFreeMemory());
        assertTrue(0 != telInfo.getMaxMemory());
        assertTrue(0 != telInfo.getTotalMemory());

        byte[] data = Externalize.toByteArray(telInfo);
        assertNotNull(data);
        assertTrue(data.length > 0);

        TelemetryInfo newInfo = new TelemetryInfo();
        assertTrue(0 == newInfo.getProcessCpuLoad());
        assertTrue(0 == newInfo.getProcessCpuTime());
        assertTrue(0 == newInfo.getSystemCpuLoad());
        assertTrue(0 == newInfo.getFreeMemory());
        assertTrue(0 == newInfo.getMaxMemory());
        assertTrue(0 == newInfo.getTotalMemory());

        Externalize.fromByteArray(data, newInfo);
        assertTrue(0 != newInfo.getProcessCpuLoad());
        assertTrue(0 != newInfo.getProcessCpuTime());
        assertTrue(0 != newInfo.getSystemCpuLoad());
        assertTrue(0 != newInfo.getFreeMemory());
        assertTrue(0 != newInfo.getMaxMemory());
        assertTrue(0 != newInfo.getTotalMemory());
    }

    public void testJoinpointInfo() throws ClassNotFoundException, IOException {
        String joinpoint = "com.test.mypackage.MyClass.funcF(java.lang.String[])";
        String filename = "MyClass.java";
        int linenr = 127;
        int calleeId = 13;
        long timestamp = System.currentTimeMillis();
        long nanotime = System.nanoTime();
        JoinpointInfo.EventType eventType = JoinpointInfo.EventType.RETURN;
        long threadId = Thread.currentThread().getId();

        JoinpointInfo pointInfo = new JoinpointInfo(joinpoint, filename,
                linenr, calleeId, timestamp, nanotime, eventType, threadId);
        assertEquals(joinpoint, pointInfo.getJoinpoint());
        assertEquals(filename, pointInfo.getFilename());
        assertEquals(linenr, pointInfo.getLinenr());
        assertEquals(calleeId, pointInfo.getIdHashCode());
        assertEquals(timestamp, pointInfo.getTimestamp());
        assertEquals(nanotime, pointInfo.getNanotime());
        assertEquals(eventType, pointInfo.getEventType());
        assertEquals(threadId, pointInfo.getThreadId());

        byte[] data = Externalize.toByteArray(pointInfo);
        assertNotNull(data);
        assertTrue(data.length > 0);

        JoinpointInfo newInfo = new JoinpointInfo();
        assertNull(newInfo.getJoinpoint());
        assertNull(newInfo.getFilename());
        assertTrue(newInfo.getLinenr() == 0);
        assertTrue(newInfo.getIdHashCode() == 0);
        assertTrue(newInfo.getTimestamp() == 0);
        assertTrue(newInfo.getNanotime() == 0);
        assertNull(newInfo.getEventType());
        assertTrue(newInfo.getThreadId() == 0);

        Externalize.fromByteArray(data, newInfo);
        assertEquals(joinpoint, newInfo.getJoinpoint());
        assertEquals(filename, newInfo.getFilename());
        assertEquals(linenr, newInfo.getLinenr());
        assertEquals(calleeId, newInfo.getIdHashCode());
        assertEquals(timestamp, newInfo.getTimestamp());
        assertEquals(nanotime, newInfo.getNanotime());
        assertEquals(eventType, newInfo.getEventType());
        assertEquals(threadId, newInfo.getThreadId());
    }

    public void testTracePacketNewCase() throws ClassNotFoundException, IOException {
        UUID uuid = UUID.randomUUID();
        String idApp = "App X";
        String idTier = "Main";
        String idNode = "Node 1";
        NodeInfo nodeInfo = new NodeInfo(uuid, idApp, idTier, idNode);

        TracePacket packet = TracePacket.createNewCase(nodeInfo);
        assertEquals(PacketType.NewCase, packet.getType());
        assertEquals(uuid, packet.getNodeInfo().getNodeSessionId());
        assertEquals(idApp, packet.getNodeInfo().getApp());
        assertEquals(idTier, packet.getNodeInfo().getTier());
        assertEquals(idNode, packet.getNodeInfo().getNode());
        assertNull(packet.getJoinpointInfo());
        assertNull(packet.getCommInfo());
        assertNull(packet.getRegions());

        byte[] data = Externalize.toByteArray(packet);
        assertNotNull(data);
        assertTrue(data.length > 0);

        TracePacket newInfo = new TracePacket();
        assertNull(newInfo.getType());
        assertNull(newInfo.getNodeInfo());
        assertNull(newInfo.getJoinpointInfo());
        assertNull(newInfo.getCommInfo());
        assertNull(newInfo.getExceptionInfo());
        assertNull(newInfo.getRegions());

        Externalize.fromByteArray(data, newInfo);
        assertEquals(PacketType.NewCase, newInfo.getType());
        assertEquals(uuid, newInfo.getNodeInfo().getNodeSessionId());
        assertEquals(idApp, newInfo.getNodeInfo().getApp());
        assertEquals(idTier, newInfo.getNodeInfo().getTier());
        assertEquals(idNode, newInfo.getNodeInfo().getNode());
        assertNull(newInfo.getJoinpointInfo());
        assertNull(newInfo.getCommInfo());
        assertNull(newInfo.getCallInfo());
        assertNull(newInfo.getExceptionInfo());
        assertNull(newInfo.getRegions());
    }

    public void testTracePacketJoinpoint() throws ClassNotFoundException, IOException {
        UUID uuid = UUID.randomUUID();
        String idApp = "App X";
        String idTier = "Main";
        String idNode = "Node 1";
        NodeInfo nodeInfo = new NodeInfo(uuid, idApp, idTier, idNode);

        String joinpoint = "com.test.mypackage.MyClass.funcF(java.lang.String[])";
        String filename = "MyClass.java";
        int linenr = 127;
        int calleeId = 13;
        long timestamp = System.currentTimeMillis();
        long nanotime = System.nanoTime();
        JoinpointInfo.EventType eventType = JoinpointInfo.EventType.RETURN;
        long threadId = Thread.currentThread().getId();
        JoinpointInfo pointInfo = new JoinpointInfo(joinpoint, filename,
                linenr, calleeId, timestamp, nanotime, eventType, threadId);

        String R1 = "A";
        String R2 = "A-B";
        String[] regions = new String[] { R1, R2 };

        TracePacket packet = TracePacket.createJoinpoint(nodeInfo,pointInfo,"pure", regions);
        assertEquals(PacketType.Joinpoint, packet.getType());
        assertEquals(uuid, packet.getNodeInfo().getNodeSessionId());
        assertEquals(idApp, packet.getNodeInfo().getApp());
        assertEquals(idTier, packet.getNodeInfo().getTier());
        assertEquals(idNode, packet.getNodeInfo().getNode());
        assertEquals(joinpoint, packet.getJoinpointInfo().getJoinpoint());
        assertEquals(filename, packet.getJoinpointInfo().getFilename());
        assertEquals(linenr, packet.getJoinpointInfo().getLinenr());
        assertEquals(timestamp, packet.getJoinpointInfo().getTimestamp());
        assertEquals(eventType, packet.getJoinpointInfo().getEventType());
        assertEquals(threadId, packet.getJoinpointInfo().getThreadId());
        assertNull(packet.getCommInfo());
        assertEquals(2, packet.getRegions().length);
        assertEquals(R1, packet.getRegions()[0]);
        assertEquals(R2, packet.getRegions()[1]);

        byte[] data = Externalize.toByteArray(packet);
        assertNotNull(data);
        assertTrue(data.length > 0);

        TracePacket newInfo = new TracePacket();
        assertNull(newInfo.getType());
        assertNull(newInfo.getNodeInfo());
        assertNull(newInfo.getJoinpointInfo());
        assertNull(newInfo.getCommInfo());
        assertNull(newInfo.getCallInfo());
        assertNull(newInfo.getExceptionInfo());
        assertNull(newInfo.getRegions());

        Externalize.fromByteArray(data, newInfo);
        assertEquals(PacketType.Joinpoint, newInfo.getType());
        assertEquals(uuid, newInfo.getNodeInfo().getNodeSessionId());
        assertEquals(idApp, newInfo.getNodeInfo().getApp());
        assertEquals(idTier, newInfo.getNodeInfo().getTier());
        assertEquals(idNode, newInfo.getNodeInfo().getNode());
        assertEquals(joinpoint, newInfo.getJoinpointInfo().getJoinpoint());
        assertEquals(filename, newInfo.getJoinpointInfo().getFilename());
        assertEquals(linenr, newInfo.getJoinpointInfo().getLinenr());
        assertEquals(timestamp, newInfo.getJoinpointInfo().getTimestamp());
        assertEquals(eventType, newInfo.getJoinpointInfo().getEventType());
        assertEquals(threadId, newInfo.getJoinpointInfo().getThreadId());
        assertNull(newInfo.getCommInfo());
        assertEquals(2, newInfo.getRegions().length);
        assertEquals(R1, newInfo.getRegions()[0]);
        assertEquals(R2, newInfo.getRegions()[1]);
    }

/*
    public void testTracePacketJoinpointParams() throws ClassNotFoundException, IOException {
        UUID uuid = UUID.randomUUID();
        String idApp = "App X";
        String idTier = "Main";
        String idNode = "Node 1";
        NodeInfo nodeInfo = new NodeInfo(uuid, idApp, idTier, idNode);

        String joinpoint = "com.test.mypackage.MyClass.funcF(java.lang.String[])";
        String filename = "MyClass.java";
        int linenr = 127;
        int calleeId = 13;
        long timestamp = System.currentTimeMillis();
        long nanotime = System.nanoTime();
        JoinpointInfo.EventType eventType = JoinpointInfo.EventType.RETURN;
        long threadId = Thread.currentThread().getId();
        JoinpointInfo pointInfo = new JoinpointInfo(joinpoint, filename,
                linenr, calleeId, timestamp, nanotime, eventType, threadId);
        pointInfo.setParams(new String[] {
            "int",
        }, new long[] {
            1243,
        });

        String R1 = "A";
        String R2 = "A-B";
        String[] regions = new String[] { R1, R2 };

        TracePacket packet = TracePacket.createJoinpoint(nodeInfo, pointInfo, regions);
        assertEquals(PacketType.Joinpoint, packet.getType());
        assertEquals(uuid, packet.getNodeInfo().getNodeSessionId());
        assertEquals(idApp, packet.getNodeInfo().getApp());
        assertEquals(idTier, packet.getNodeInfo().getTier());
        assertEquals(idNode, packet.getNodeInfo().getNode());
        assertEquals(joinpoint, packet.getJoinpointInfo().getJoinpoint());
        assertEquals(filename, packet.getJoinpointInfo().getFilename());
        assertEquals(linenr, packet.getJoinpointInfo().getLinenr());
        assertEquals(timestamp, packet.getJoinpointInfo().getTimestamp());
        assertEquals(eventType, packet.getJoinpointInfo().getEventType());
        assertEquals(threadId, packet.getJoinpointInfo().getThreadId());
        assertNull(packet.getCommInfo());
        assertEquals(2, packet.getRegions().length);
        assertEquals(R1, packet.getRegions()[0]);
        assertEquals(R2, packet.getRegions()[1]);

        byte[] data = Externalize.toByteArray(packet);
        assertNotNull(data);
        assertTrue(data.length > 0);

        TracePacket newInfo = new TracePacket();
        assertNull(newInfo.getType());
        assertNull(newInfo.getNodeInfo());
        assertNull(newInfo.getJoinpointInfo());
        assertNull(newInfo.getCommInfo());
        assertNull(newInfo.getCallInfo());
        assertNull(newInfo.getExceptionInfo());
        assertNull(newInfo.getRegions());

        Externalize.fromByteArray(data, newInfo);
        assertEquals(PacketType.Joinpoint, newInfo.getType());
        assertEquals(uuid, newInfo.getNodeInfo().getNodeSessionId());
        assertEquals(idApp, newInfo.getNodeInfo().getApp());
        assertEquals(idTier, newInfo.getNodeInfo().getTier());
        assertEquals(idNode, newInfo.getNodeInfo().getNode());
        assertEquals(joinpoint, newInfo.getJoinpointInfo().getJoinpoint());
        assertEquals(filename, newInfo.getJoinpointInfo().getFilename());
        assertEquals(linenr, newInfo.getJoinpointInfo().getLinenr());
        assertEquals(timestamp, newInfo.getJoinpointInfo().getTimestamp());
        assertEquals(eventType, newInfo.getJoinpointInfo().getEventType());
        assertEquals(threadId, newInfo.getJoinpointInfo().getThreadId());
        assertNull(newInfo.getCommInfo());
        assertEquals(2, newInfo.getRegions().length);
        assertEquals(R1, newInfo.getRegions()[0]);
        assertEquals(R2, newInfo.getRegions()[1]);

        assertEquals(3, newInfo.getJoinpointInfo().getParamTypes().length);

        assertEquals(JoinpointInfo.DataType.ObjectId, newInfo.getJoinpointInfo().getParamTypes()[0]);
        assertEquals(JoinpointInfo.DataType.Float, newInfo.getJoinpointInfo().getParamTypes()[1]);
        assertEquals(JoinpointInfo.DataType.Int, newInfo.getJoinpointInfo().getParamTypes()[2]);

        assertEquals(1243, newInfo.getJoinpointInfo().getParamValues()[0]);
        assertEquals(12.5, Double.longBitsToDouble(newInfo.getJoinpointInfo().getParamValues()[1]));
        assertEquals(5645, newInfo.getJoinpointInfo().getParamValues()[2]);

        assertEquals(null, newInfo.getJoinpointInfo().getReturnType());
        assertEquals(0, newInfo.getJoinpointInfo().getReturnValue());
    }

    public void testTracePacketJoinpointParamsReturn() throws ClassNotFoundException, IOException {
        UUID uuid = UUID.randomUUID();
        String idApp = "App X";
        String idTier = "Main";
        String idNode = "Node 1";
        NodeInfo nodeInfo = new NodeInfo(uuid, idApp, idTier, idNode);

        String joinpoint = "com.test.mypackage.MyClass.funcF(java.lang.String[])";
        String filename = "MyClass.java";
        int linenr = 127;
        int calleeId = 13;
        long timestamp = System.currentTimeMillis();
        long nanotime = System.nanoTime();
        JoinpointInfo.EventType eventType = JoinpointInfo.EventType.RETURN;
        long threadId = Thread.currentThread().getId();
        JoinpointInfo pointInfo = new JoinpointInfo(joinpoint, filename,
                linenr, calleeId, timestamp, nanotime, eventType, threadId);
        pointInfo.setParams(new String[] {
            "int",
        }, new long[] {
            1243,
        }).setReturn(1,"Int", 5);

        String R1 = "A";
        String R2 = "A-B";
        String[] regions = new String[] { R1, R2 };

        TracePacket packet = TracePacket.createJoinpoint(nodeInfo, pointInfo, regions);
        assertEquals(PacketType.Joinpoint, packet.getType());
        assertEquals(uuid, packet.getNodeInfo().getNodeSessionId());
        assertEquals(idApp, packet.getNodeInfo().getApp());
        assertEquals(idTier, packet.getNodeInfo().getTier());
        assertEquals(idNode, packet.getNodeInfo().getNode());
        assertEquals(joinpoint, packet.getJoinpointInfo().getJoinpoint());
        assertEquals(filename, packet.getJoinpointInfo().getFilename());
        assertEquals(linenr, packet.getJoinpointInfo().getLinenr());
        assertEquals(timestamp, packet.getJoinpointInfo().getTimestamp());
        assertEquals(eventType, packet.getJoinpointInfo().getEventType());
        assertEquals(threadId, packet.getJoinpointInfo().getThreadId());
        assertNull(packet.getCommInfo());
        assertEquals(2, packet.getRegions().length);
        assertEquals(R1, packet.getRegions()[0]);
        assertEquals(R2, packet.getRegions()[1]);

        byte[] data = Externalize.toByteArray(packet);
        assertNotNull(data);
        assertTrue(data.length > 0);

        TracePacket newInfo = new TracePacket();
        assertNull(newInfo.getType());
        assertNull(newInfo.getNodeInfo());
        assertNull(newInfo.getJoinpointInfo());
        assertNull(newInfo.getCommInfo());
        assertNull(newInfo.getCallInfo());
        assertNull(newInfo.getExceptionInfo());
        assertNull(newInfo.getRegions());

        Externalize.fromByteArray(data, newInfo);
        assertEquals(PacketType.Joinpoint, newInfo.getType());
        assertEquals(uuid, newInfo.getNodeInfo().getNodeSessionId());
        assertEquals(idApp, newInfo.getNodeInfo().getApp());
        assertEquals(idTier, newInfo.getNodeInfo().getTier());
        assertEquals(idNode, newInfo.getNodeInfo().getNode());
        assertEquals(joinpoint, newInfo.getJoinpointInfo().getJoinpoint());
        assertEquals(filename, newInfo.getJoinpointInfo().getFilename());
        assertEquals(linenr, newInfo.getJoinpointInfo().getLinenr());
        assertEquals(timestamp, newInfo.getJoinpointInfo().getTimestamp());
        assertEquals(eventType, newInfo.getJoinpointInfo().getEventType());
        assertEquals(threadId, newInfo.getJoinpointInfo().getThreadId());
        assertNull(newInfo.getCommInfo());
        assertEquals(2, newInfo.getRegions().length);
        assertEquals(R1, newInfo.getRegions()[0]);
        assertEquals(R2, newInfo.getRegions()[1]);

        assertEquals(3, newInfo.getJoinpointInfo().getParamTypes().length);

        assertEquals(JoinpointInfo.DataType.ObjectId, newInfo.getJoinpointInfo().getParamTypes()[0]);
        assertEquals(JoinpointInfo.DataType.Float, newInfo.getJoinpointInfo().getParamTypes()[1]);
        assertEquals(JoinpointInfo.DataType.Int, newInfo.getJoinpointInfo().getParamTypes()[2]);

        assertEquals(1243, newInfo.getJoinpointInfo().getParamValues()[0]);
        assertEquals(12.5, Double.longBitsToDouble(newInfo.getJoinpointInfo().getParamValues()[1]));
        assertEquals(5645, newInfo.getJoinpointInfo().getParamValues()[2]);

        assertEquals(JoinpointInfo.DataType.Int, newInfo.getJoinpointInfo().getReturnType());
        assertEquals(5, newInfo.getJoinpointInfo().getReturnValue());
    }*/

    public void testTracePacketCommJoinpoint() throws ClassNotFoundException, IOException {
        UUID uuid = UUID.randomUUID();
        String idApp = "App X";
        String idTier = "Main";
        String idNode = "Node 1";
        NodeInfo nodeInfo = new NodeInfo(uuid, idApp, idTier, idNode);

        String joinpoint = "com.test.mypackage.MyClass.funcF(java.lang.String[])";
        String filename = "MyClass.java";
        int linenr = 127;
        int calleeId = 13;
        long timestamp = System.currentTimeMillis();
        long nanotime = System.nanoTime();
        JoinpointInfo.EventType eventType = JoinpointInfo.EventType.RETURN;
        long threadId = Thread.currentThread().getId();
        JoinpointInfo pointInfo = new JoinpointInfo(joinpoint, filename,
                linenr, calleeId, timestamp, nanotime, eventType, threadId);

        String localHost = "127.0.0.1";
        int localPort = 9000;
        String remoteHost = "127.20.21.22";
        int remotePort = 9012;
        CommInfo commInfo = new CommInfo(localHost, localPort, remoteHost,
                remotePort);

        String[] regions = new String[] { };

        TracePacket packet = TracePacket.createCommJoinpoint(nodeInfo, pointInfo, commInfo, regions);
        assertEquals(PacketType.CommJoinpoint, packet.getType());
        assertEquals(uuid, packet.getNodeInfo().getNodeSessionId());
        assertEquals(idApp, packet.getNodeInfo().getApp());
        assertEquals(idTier, packet.getNodeInfo().getTier());
        assertEquals(idNode, packet.getNodeInfo().getNode());
        assertEquals(joinpoint, packet.getJoinpointInfo().getJoinpoint());
        assertEquals(filename, packet.getJoinpointInfo().getFilename());
        assertEquals(linenr, packet.getJoinpointInfo().getLinenr());
        assertEquals(timestamp, packet.getJoinpointInfo().getTimestamp());
        assertEquals(eventType, packet.getJoinpointInfo().getEventType());
        assertEquals(threadId, packet.getJoinpointInfo().getThreadId());
        assertEquals(localHost, packet.getCommInfo().getLocalHost());
        assertEquals(localPort, packet.getCommInfo().getLocalPort());
        assertEquals(remoteHost, packet.getCommInfo().getRemoteHost());
        assertEquals(remotePort, packet.getCommInfo().getRemotePort());
        assertEquals(0, packet.getRegions().length);

        byte[] data = Externalize.toByteArray(packet);
        assertNotNull(data);
        assertTrue(data.length > 0);

        TracePacket newInfo = new TracePacket();
        assertNull(newInfo.getType());
        assertNull(newInfo.getNodeInfo());
        assertNull(newInfo.getJoinpointInfo());
        assertNull(newInfo.getCommInfo());
        assertNull(newInfo.getCallInfo());
        assertNull(newInfo.getExceptionInfo());
        assertNull(newInfo.getRegions());

        Externalize.fromByteArray(data, newInfo);
        assertEquals(PacketType.CommJoinpoint, newInfo.getType());
        assertEquals(uuid, newInfo.getNodeInfo().getNodeSessionId());
        assertEquals(idApp, newInfo.getNodeInfo().getApp());
        assertEquals(idTier, newInfo.getNodeInfo().getTier());
        assertEquals(idNode, newInfo.getNodeInfo().getNode());
        assertEquals(joinpoint, newInfo.getJoinpointInfo().getJoinpoint());
        assertEquals(filename, newInfo.getJoinpointInfo().getFilename());
        assertEquals(linenr, newInfo.getJoinpointInfo().getLinenr());
        assertEquals(timestamp, newInfo.getJoinpointInfo().getTimestamp());
        assertEquals(eventType, newInfo.getJoinpointInfo().getEventType());
        assertEquals(threadId, newInfo.getJoinpointInfo().getThreadId());
        assertEquals(localHost, newInfo.getCommInfo().getLocalHost());
        assertEquals(localPort, newInfo.getCommInfo().getLocalPort());
        assertEquals(remoteHost, newInfo.getCommInfo().getRemoteHost());
        assertEquals(remotePort, newInfo.getCommInfo().getRemotePort());
        assertEquals(0, newInfo.getRegions().length);
    }


    public void testTracePacketCallJoinpoint() throws ClassNotFoundException, IOException {
        UUID uuid = UUID.randomUUID();
        String idApp = "App X";
        String idTier = "Main";
        String idNode = "Node 1";
        NodeInfo nodeInfo = new NodeInfo(uuid, idApp, idTier, idNode);

        String joinpoint = "com.test.mypackage.MyClass.funcF(java.lang.String[])";
        String filename = "MyClass.java";
        int linenr = 127;
        int calleeId = 13;
        long timestamp = System.currentTimeMillis();
        long nanotime = System.nanoTime();
        JoinpointInfo.EventType eventType = JoinpointInfo.EventType.RETURN;
        long threadId = Thread.currentThread().getId();
        JoinpointInfo pointInfo = new JoinpointInfo(joinpoint, filename,
                linenr, calleeId, timestamp, nanotime, eventType, threadId);

        int callerId = 12;
        String callerJoinpoint = "com.test.mypackage.MyClass22.funcG(int)";
        String callerFilename = "MyClass22.java";
        int callerLinenr = 56;
        CallInfo callInfo = new CallInfo(callerId, callerJoinpoint, callerFilename,
                callerLinenr);

        String[] regions = new String[] { };

        TracePacket packet = TracePacket.createCallJoinpoint(nodeInfo, pointInfo, callInfo, regions);
        assertEquals(PacketType.CallJoinpoint, packet.getType());
        assertEquals(uuid, packet.getNodeInfo().getNodeSessionId());
        assertEquals(idApp, packet.getNodeInfo().getApp());
        assertEquals(idTier, packet.getNodeInfo().getTier());
        assertEquals(idNode, packet.getNodeInfo().getNode());
        assertEquals(joinpoint, packet.getJoinpointInfo().getJoinpoint());
        assertEquals(filename, packet.getJoinpointInfo().getFilename());
        assertEquals(linenr, packet.getJoinpointInfo().getLinenr());
        assertEquals(calleeId, packet.getJoinpointInfo().getIdHashCode());
        assertEquals(timestamp, packet.getJoinpointInfo().getTimestamp());
        assertEquals(eventType, packet.getJoinpointInfo().getEventType());
        assertEquals(threadId, packet.getJoinpointInfo().getThreadId());
        assertEquals(callerId, packet.getCallInfo().getCallerIdHashCode());
        assertEquals(callerJoinpoint, packet.getCallInfo().getCallerJoinpoint());
        assertEquals(callerFilename, packet.getCallInfo().getCallerFilename());
        assertEquals(callerLinenr, packet.getCallInfo().getCallerLinenr());
        assertEquals(0, packet.getRegions().length);

        byte[] data = Externalize.toByteArray(packet);
        assertNotNull(data);
        assertTrue(data.length > 0);

        TracePacket newInfo = new TracePacket();
        assertNull(newInfo.getType());
        assertNull(newInfo.getNodeInfo());
        assertNull(newInfo.getJoinpointInfo());
        assertNull(newInfo.getCommInfo());
        assertNull(newInfo.getCallInfo());
        assertNull(newInfo.getExceptionInfo());
        assertNull(newInfo.getRegions());

        Externalize.fromByteArray(data, newInfo);
        assertEquals(PacketType.CallJoinpoint, newInfo.getType());
        assertEquals(uuid, newInfo.getNodeInfo().getNodeSessionId());
        assertEquals(idApp, newInfo.getNodeInfo().getApp());
        assertEquals(idTier, newInfo.getNodeInfo().getTier());
        assertEquals(idNode, newInfo.getNodeInfo().getNode());
        assertEquals(joinpoint, newInfo.getJoinpointInfo().getJoinpoint());
        assertEquals(filename, newInfo.getJoinpointInfo().getFilename());
        assertEquals(linenr, newInfo.getJoinpointInfo().getLinenr());
        assertEquals(calleeId, newInfo.getJoinpointInfo().getIdHashCode());
        assertEquals(timestamp, newInfo.getJoinpointInfo().getTimestamp());
        assertEquals(eventType, newInfo.getJoinpointInfo().getEventType());
        assertEquals(threadId, newInfo.getJoinpointInfo().getThreadId());
        assertEquals(callerId, newInfo.getCallInfo().getCallerIdHashCode());
        assertEquals(callerJoinpoint, newInfo.getCallInfo().getCallerJoinpoint());
        assertEquals(callerFilename, newInfo.getCallInfo().getCallerFilename());
        assertEquals(callerLinenr, newInfo.getCallInfo().getCallerLinenr());
        assertEquals(0, newInfo.getRegions().length);
    }


    public void testTracePacketExceptionJoinpoint() throws ClassNotFoundException, IOException {
        UUID uuid = UUID.randomUUID();
        String idApp = "App X";
        String idTier = "Main";
        String idNode = "Node 1";
        NodeInfo nodeInfo = new NodeInfo(uuid, idApp, idTier, idNode);

        String joinpoint = "com.test.mypackage.MyClass.funcF(java.lang.String[])";
        String filename = "MyClass.java";
        int linenr = 127;
        long timestamp = System.currentTimeMillis();
        long nanotime = System.nanoTime();
        int calleeId = 13;
        JoinpointInfo.EventType eventType = JoinpointInfo.EventType.HANDLE;
        long threadId = Thread.currentThread().getId();
        JoinpointInfo pointInfo = new JoinpointInfo(joinpoint, filename,
                linenr, calleeId, timestamp, nanotime, eventType, threadId);

        String throwType = "java.lang.IllegalStateException";
        String catchType = "java.lang.Exception";
        ExceptionInfo exceptionInfo = new ExceptionInfo(throwType, catchType);

        String[] regions = new String[] { };

        TracePacket packet = TracePacket.createExceptionJoinpoint(nodeInfo, pointInfo, exceptionInfo, regions);
        assertEquals(PacketType.ExceptionJoinpoint, packet.getType());
        assertEquals(uuid, packet.getNodeInfo().getNodeSessionId());
        assertEquals(idApp, packet.getNodeInfo().getApp());
        assertEquals(idTier, packet.getNodeInfo().getTier());
        assertEquals(idNode, packet.getNodeInfo().getNode());
        assertEquals(joinpoint, packet.getJoinpointInfo().getJoinpoint());
        assertEquals(filename, packet.getJoinpointInfo().getFilename());
        assertEquals(linenr, packet.getJoinpointInfo().getLinenr());
        assertEquals(calleeId, packet.getJoinpointInfo().getIdHashCode());
        assertEquals(timestamp, packet.getJoinpointInfo().getTimestamp());
        assertEquals(eventType, packet.getJoinpointInfo().getEventType());
        assertEquals(threadId, packet.getJoinpointInfo().getThreadId());
        assertEquals(throwType, packet.getExceptionInfo().getThrowType());
        assertEquals(catchType, packet.getExceptionInfo().getCatchType());
        assertEquals(0, packet.getRegions().length);
        byte[] data = Externalize.toByteArray(packet);
        assertNotNull(data);
        assertTrue(data.length > 0);

        TracePacket newInfo = new TracePacket();
        assertNull(newInfo.getType());
        assertNull(newInfo.getNodeInfo());
        assertNull(newInfo.getJoinpointInfo());
        assertNull(newInfo.getCommInfo());
        assertNull(newInfo.getCallInfo());
        assertNull(newInfo.getExceptionInfo());
        assertNull(newInfo.getRegions());

        Externalize.fromByteArray(data, newInfo);
        assertEquals(PacketType.ExceptionJoinpoint, newInfo.getType());
        assertEquals(uuid, newInfo.getNodeInfo().getNodeSessionId());
        assertEquals(idApp, newInfo.getNodeInfo().getApp());
        assertEquals(idTier, newInfo.getNodeInfo().getTier());
        assertEquals(idNode, newInfo.getNodeInfo().getNode());
        assertEquals(joinpoint, newInfo.getJoinpointInfo().getJoinpoint());
        assertEquals(filename, newInfo.getJoinpointInfo().getFilename());
        assertEquals(linenr, newInfo.getJoinpointInfo().getLinenr());
        assertEquals(calleeId, newInfo.getJoinpointInfo().getIdHashCode());
        assertEquals(timestamp, newInfo.getJoinpointInfo().getTimestamp());
        assertEquals(eventType, newInfo.getJoinpointInfo().getEventType());
        assertEquals(threadId, newInfo.getJoinpointInfo().getThreadId());
        assertEquals(throwType, newInfo.getExceptionInfo().getThrowType());
        assertEquals(catchType, newInfo.getExceptionInfo().getCatchType());
        assertEquals(0, newInfo.getRegions().length);
    }
}
