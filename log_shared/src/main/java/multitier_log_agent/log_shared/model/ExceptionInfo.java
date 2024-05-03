package multitier_log_agent.log_shared.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ExceptionInfo implements Externalizable {

    public static final String NameUncaught = "<uncaught>";
    public static final String NameFinally = "<finally>";
    
    private String throwType;
    private String catchType;

    public ExceptionInfo() {
        
    }
    
    public ExceptionInfo(String throwType, String catchType) {
        this.throwType = throwType;
        this.catchType = catchType;
    }
    
    public String getThrowType() {
        return throwType;
    }
    public String getCatchType() {
        return catchType;
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(this.throwType);
        out.writeUTF(this.catchType);
    }

    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        this.throwType = in.readUTF();
        this.catchType = in.readUTF();
    }
}
