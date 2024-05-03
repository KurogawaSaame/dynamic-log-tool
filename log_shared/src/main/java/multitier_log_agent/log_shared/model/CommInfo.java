package multitier_log_agent.log_shared.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class CommInfo implements Externalizable {

    private String localHost;
    private int localPort;
    private String remoteHost;
    private int remotePort;

    public CommInfo() {

    }

    public CommInfo(String localHost, int localPort, String remoteHost,
            int remotePort) {
        this.localHost = localHost;
        this.localPort = localPort;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }
    
    public String getLocalHost() {
        return localHost;
    }

    public int getLocalPort() {
        return localPort;
    }
    
    public String getRemoteHost() {
        return remoteHost;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(localHost);
        out.writeInt(localPort);
        out.writeUTF(remoteHost);
        out.writeInt(remotePort);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        localHost = in.readUTF();
        localPort = in.readInt();
        remoteHost = in.readUTF();
        remotePort = in.readInt();
    }
}
