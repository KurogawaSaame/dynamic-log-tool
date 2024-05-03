package multitier_log_agent.log_shared.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

public class NodeInfo implements Externalizable {

    private UUID nodeSessionId;
    private String app;
    private String tier;
    private String node;

    public NodeInfo() {
        
    }
    
    public NodeInfo(String app, String tier, String node) {
        this(UUID.randomUUID(), app, tier, node);
    }

    public NodeInfo(UUID nodeSessionId, String app, String tier, String node) {
        this.nodeSessionId = nodeSessionId;
        this.app = app;
        this.tier = tier;
        this.node = node;
    }
    
    public UUID getNodeSessionId() {
        return nodeSessionId;
    }
    
    public String getApp() {
        return app;
    }
    
    public String getTier() {
        return tier;
    }
    
    public String getNode() {
        return node;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(nodeSessionId.getMostSignificantBits());
        out.writeLong(nodeSessionId.getLeastSignificantBits());
        out.writeUTF(app);
        out.writeUTF(tier);
        out.writeUTF(node);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        nodeSessionId = new UUID(in.readLong(), in.readLong());
        app = in.readUTF();
        tier = in.readUTF();
        node = in.readUTF();
    }
    
}
