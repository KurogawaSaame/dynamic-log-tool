package multitier_log_agent.log_shared.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class CallInfo implements Externalizable {

    private int callerIdHashCode;
    private String callerJoinpoint;
    private String callerFilename;
    private int callerLinenr;

    public CallInfo() {
        
    }
    
    public CallInfo(int callerIdHashCode, String callerJoinpoint, 
            String callerFilename, int callerLinenr) {
        this.callerIdHashCode = callerIdHashCode;
        this.callerJoinpoint = callerJoinpoint;
        this.callerFilename = callerFilename;
        this.callerLinenr = callerLinenr;
    }

    public int getCallerIdHashCode() {
        return callerIdHashCode;
    }

    public String getCallerJoinpoint() {
        return callerJoinpoint;
    }

    public String getCallerFilename() {
        return callerFilename;
    }

    public int getCallerLinenr() {
        return callerLinenr;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(this.callerIdHashCode);
        out.writeUTF(this.callerJoinpoint);
        out.writeUTF(this.callerFilename);
        out.writeInt(this.callerLinenr);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        this.callerIdHashCode = in.readInt();
        this.callerJoinpoint = in.readUTF();
        this.callerFilename = in.readUTF();
        this.callerLinenr = in.readInt();
    }
}
