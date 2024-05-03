package multitier_log_agent.log_server.model;

import multitier_log_agent.log_shared.model.TracePacket;

public class Event {

    public final int caseId;
    public final TracePacket packet;
    
    public Event(int caseId, TracePacket packet) {
        this.caseId = caseId;
        this.packet = packet;
    }
    
}
