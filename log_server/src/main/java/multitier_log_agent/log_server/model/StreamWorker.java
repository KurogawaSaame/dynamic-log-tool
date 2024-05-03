package multitier_log_agent.log_server.model;

import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import multitier_log_agent.log_server.util.signals.Signal0;
import multitier_log_agent.log_server.util.signals.Signal1;
import multitier_log_agent.log_shared.model.PacketType;
import multitier_log_agent.log_shared.model.TracePacket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.clearspring.analytics.stream.StreamSummary;

public class StreamWorker implements Runnable {

    public static Logger logger = LogManager.getLogger(StreamWorker.class);

    /**
     * Case with given id is created
     */
    public final Signal1<Integer> SignalNewCase = new Signal1<>();

    /**
     * New event is to be available
     */
    public final Signal1<Event> SignalNewEvent = new Signal1<>();

    /**
     * Case with given id is finished / closed / flushed
     */
    public final Signal1<Integer> SignalCaseFinished = new Signal1<>();

    /**
     * Shutdown signal, all cases should be closed / flushed
     */
    public final Signal0 SignalFinish = new Signal0();

    /**
     * Threading
     */
    private boolean doRun = true;
    private final BlockingQueue<TracePacket> commQueue;

    /**
     * Case tracking
     */
    private final StreamSummary<UUID> sessionStream;
    private final TObjectIntMap<UUID> sessionCaseId;
    private int nextCaseId;

    public StreamWorker(BlockingQueue<TracePacket> commQueue, int maxCaseTrack) {
        this.commQueue = commQueue;

        sessionStream = new StreamSummary<>(maxCaseTrack);
        sessionCaseId = new TObjectIntHashMap<>(maxCaseTrack);
        nextCaseId = 1;
    }

    public void run() {
        doRun = true;
        while (doRun && !Thread.interrupted()) {
            try {
                _processPacket(commQueue.take());
            } catch (InterruptedException e) {
                // App.logger.info("Model consumer thread interrupted", e);
                break;
            }
        }
        SignalFinish.dispatch();
    }

    public void requestStop() {
        doRun = false;
    }

    private void _processPacket(TracePacket packet) {
        UUID currentSession = packet.getNodeInfo().getNodeSessionId();
        UUID droppedCase = sessionStream.offerReturnDropped(currentSession, 1);
        if (droppedCase != null) {
            _handleFinishedCase(droppedCase);
        }

        boolean isNewCasePacket = packet.getType() == PacketType.NewCase;

        int caseId = sessionCaseId.get(currentSession);
        if (caseId == Constants.DEFAULT_INT_NO_ENTRY_VALUE || isNewCasePacket) {
            caseId = nextCaseId;
            nextCaseId++;
            sessionCaseId.put(currentSession, caseId);
            logger.debug("Start new case #" + Integer.toString(caseId) + " for session " + currentSession.toString());
            SignalNewCase.dispatch(caseId);
        }

        if (!isNewCasePacket) {
            SignalNewEvent.dispatch(new Event(caseId, packet));
        }
    }

    private void _handleFinishedCase(UUID caseToEnd) {
        int caseId = sessionCaseId.remove(caseToEnd);
        if (caseId != Constants.DEFAULT_INT_NO_ENTRY_VALUE) {
            logger.debug("Drop case #" + Integer.toString(caseId) + " for session " + caseToEnd.toString());
            SignalCaseFinished.dispatch(caseId);
        }
    }
}
