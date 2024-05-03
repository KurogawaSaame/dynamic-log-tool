package multitier_log_agent.log_shared.model;

import java.io.IOException;

import junit.framework.TestCase;

public class PerformanceTest extends TestCase {

    private static final int warmup = 10000;
    private static final int run = 1000000;

    public void testNew() throws ClassNotFoundException, IOException {
        String idApp = "App X";
        String idTier = "Main";
        String idNode = "Node 1";
        NodeInfo nodeInfo = new NodeInfo(idApp, idTier, idNode);

        String joinpoint = "com.test.mypackage.MyClass.funcF(java.lang.String[])";
        String filename = "MyClass.java";
        int linenr = 127;
        long timestamp = System.currentTimeMillis();
        long nanotime = System.nanoTime();
        int calleeId = 13;
        JoinpointInfo.EventType eventType = JoinpointInfo.EventType.RETURN;
        long threadId = Thread.currentThread().getId();

        String R1 = "A";
        String R2 = "A-B";
        String[] regions = new String[] { R1, R2 };

        // warmup
        for (int i = 0; i < warmup; i++) {
            JoinpointInfo pointInfo = new JoinpointInfo(joinpoint, filename,
                    linenr, calleeId, timestamp, nanotime, eventType, threadId);
            TracePacket packet = TracePacket.createJoinpoint(nodeInfo, pointInfo, "pure",regions);
            byte[] data = Externalize.toByteArray(packet);
            assertNotNull(data);
        }

        // run
        long start = System.nanoTime();
        for (int i = 0; i < run; i++) {
            JoinpointInfo pointInfo = new JoinpointInfo(joinpoint, filename,
                    linenr, calleeId, timestamp, nanotime, eventType, threadId);
            TracePacket packet = TracePacket.createJoinpoint(nodeInfo, pointInfo,"pure", regions);
            byte[] data = Externalize.toByteArray(packet);
            assertNotNull(data);
        }
        long end = System.nanoTime();

        System.out.printf("Time New: %.3f ms\n", (end - start) / 1000.0 / 1000.0);
    }

    public void testOld()  {
        String idApp = "App X";
        String idTier = "Main";
        String idNode = "Node 1";
        OldLogClient.setup(idApp, idTier, idNode);

        String joinpoint = "com.test.mypackage.MyClass.funcF(java.lang.String[])";
        //String filename = "MyClass.java";
        //int linenr = 127;
        long timestamp = System.currentTimeMillis();
        String entry = "complete";
        long threadId = Thread.currentThread().getId();

        String R1 = "A";
        String R2 = "A-B";
        String regions = R1 + "," + R2;

        // warmup
        for (int i = 0; i < warmup; i++) {
            byte[] data = OldLogClient.recordEvent(joinpoint, entry, timestamp, threadId, regions);
            assertNotNull(data);
        }

        // run
        long start = System.nanoTime();
        for (int i = 0; i < run; i++) {
            byte[] data = OldLogClient.recordEvent(joinpoint, entry, timestamp, threadId, regions);
            assertNotNull(data);
        }
        long end = System.nanoTime();

        System.out.printf("Time Old: %.3f ms\n", (end - start) / 1000.0 / 1000.0);
    }
}
