package multitier_log_agent.log_shared.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

public class TracePacket implements Externalizable {

    private PacketType type;
    private NodeInfo nodeInfo;
    private JoinpointInfo joinpointInfo;
    private CommInfo commInfo;
    private CallInfo callInfo;
    private ExceptionInfo exceptionInfo;
    private TelemetryInfo telemetryInfo;
    private String[] regions;
    private ThreadInfo threadInfo;
    private String pattern="pure";

    public static TracePacket createNewCase(NodeInfo nodeInfo) {
        TracePacket inst = new TracePacket();
        inst.type = PacketType.NewCase;
        inst.nodeInfo = nodeInfo;
        return inst;
    }

    public static TracePacket createJoinpoint(NodeInfo nodeInfo, JoinpointInfo joinpointInfo, String pattern, String[] regions) {
        TracePacket inst = new TracePacket();
        inst.type = PacketType.Joinpoint;
        inst.nodeInfo = nodeInfo;
        inst.joinpointInfo = joinpointInfo;
        inst.regions = regions;
        inst.pattern=pattern;
        return inst;
    }
    public static TracePacket createJoinpoint_Test(NodeInfo nodeInfo, JoinpointInfo joinpointInfo, String pattern, String[] regions) {
        TracePacket inst = new TracePacket();
        inst.type = PacketType.Joinpoint;
        inst.nodeInfo = nodeInfo;
        inst.joinpointInfo = joinpointInfo;
        inst.regions = regions;
        inst.pattern=pattern;
        return inst;
    }

    public static TracePacket createCommJoinpoint(NodeInfo nodeInfo,
            JoinpointInfo joinpointInfo, CommInfo commInfo,
            String[] regions) {
        TracePacket inst = new TracePacket();
        inst.type = PacketType.CommJoinpoint;
        inst.nodeInfo = nodeInfo;
        inst.joinpointInfo = joinpointInfo;
        inst.commInfo = commInfo;
        inst.regions = regions;
        return inst;
    }

    public static TracePacket createCallJoinpoint(NodeInfo nodeInfo,
            JoinpointInfo joinpointInfo, CallInfo callInfo,
            String[] regions) {
        TracePacket inst = new TracePacket();
        inst.type = PacketType.CallJoinpoint;
        inst.nodeInfo = nodeInfo;
        inst.joinpointInfo = joinpointInfo;
        inst.callInfo = callInfo;
        inst.regions = regions;
        return inst;
    }

    public static TracePacket createExceptionJoinpoint(NodeInfo nodeInfo,
            JoinpointInfo joinpointInfo, ExceptionInfo exceptionInfo,
            String[] regions) {
        TracePacket inst = new TracePacket();
        inst.type = PacketType.ExceptionJoinpoint;
        inst.nodeInfo = nodeInfo;
        inst.joinpointInfo = joinpointInfo;
        inst.exceptionInfo = exceptionInfo;
        inst.regions = regions;
        return inst;
    }

    public static TracePacket createThreadJoinpoint(NodeInfo nodeInfo,
    		JoinpointInfo joinpointInfo,ThreadInfo threadInfo){
    	TracePacket inst=new TracePacket();
    	inst.type=PacketType.ThreadJoinpoint;
    	inst.nodeInfo=nodeInfo;
    	inst.joinpointInfo=joinpointInfo;
    	inst.threadInfo=threadInfo;
    	return inst;
    }

    public TracePacket() {

    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public PacketType getType() {
        return type;
    }

    public NodeInfo getNodeInfo() {
        return nodeInfo;
    }

    public JoinpointInfo getJoinpointInfo() {
        return joinpointInfo;
    }

    public CommInfo getCommInfo() {
        return commInfo;
    }

    public CallInfo getCallInfo() {
        return callInfo;
    }

    public ExceptionInfo getExceptionInfo() {
        return exceptionInfo;
    }

    public String[] getRegions() {
        return regions;
    }

    public byte[] toByteArray() {
        return Externalize.toByteArray(this);
    }



    public void fromByteArray(byte[] objectBytes)
            throws ClassNotFoundException, IOException {
        Externalize.fromByteArray(objectBytes, this);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeByte(type.ordinal());
        nodeInfo.writeExternal(out);
        if (type != PacketType.NewCase) {
            out.writeUTF(pattern);
            switch (pattern) {
                case "pure":
                    joinpointInfo.writeExternal_0(out);
                    break;
                case "common":
                    joinpointInfo.writeExternal(out);
                    break;
                case "param":
                    joinpointInfo.writeExternal2(out);
                    break;
                case "FUP":
                    System.out.println("_________________record FUP:"+joinpointInfo.getReturnTypeFUP());
                    joinpointInfo.writeExternal_FUP(out);
                    break;
            }
            if (type == PacketType.CommJoinpoint) {
                commInfo.writeExternal(out);
            } else if (type == PacketType.CallJoinpoint) {
                callInfo.writeExternal(out);
            } else if (type == PacketType.ExceptionJoinpoint) {
                exceptionInfo.writeExternal(out);
            }

            if (regions == null) {
                out.writeInt(0);
            } else {
                out.writeInt(regions.length);
                for (int i = 0; i < regions.length; i++) {
                    out.writeUTF(regions[i]);
                }
            }
        }
    }

    //xq修改
    public void writeExternal2(ObjectOutput out) throws IOException {
        out.writeByte(type.ordinal());
        nodeInfo.writeExternal(out);

        if (type != PacketType.NewCase) {
            joinpointInfo.writeExternal2(out);

            if (type == PacketType.CommJoinpoint) {
                commInfo.writeExternal(out);
            } else if (type == PacketType.CallJoinpoint) {
                callInfo.writeExternal(out);
            } else if (type == PacketType.ExceptionJoinpoint) {
                exceptionInfo.writeExternal(out);
            }

            if (regions == null) {
                out.writeInt(0);
            } else {
                out.writeInt(regions.length);
                for (int i = 0; i < regions.length; i++) {
                    out.writeUTF(regions[i]);
                }
            }
        }
    }


    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        type = PacketType.values()[in.readByte()];
        nodeInfo = new NodeInfo();
        nodeInfo.readExternal(in);

        if (type != PacketType.NewCase) {
            pattern=in.readUTF();
            joinpointInfo = new JoinpointInfo();
            switch (pattern) {
                case "pure":
                    joinpointInfo.readExternal_0(in);
                    break;
                case "common":
                    joinpointInfo.readExternal(in);
                    break;
                case "param":
                    joinpointInfo.readExternal2(in);
                    break;
                case "FUP":
                    joinpointInfo.readExternal_FUP(in);
                    break;
            }

            if (type == PacketType.CommJoinpoint) {
                commInfo = new CommInfo();
                commInfo.readExternal(in);
            } else if (type == PacketType.CallJoinpoint) {
                callInfo = new CallInfo();
                callInfo.readExternal(in);
            } else if (type == PacketType.ExceptionJoinpoint) {
                exceptionInfo = new ExceptionInfo();
                exceptionInfo.readExternal(in);
            }

            regions = new String[in.readInt()];
            for (int i = 0; i < regions.length; i++) {
                regions[i] = in.readUTF();
            }
        }
    }

	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append("TracePacket [type=" + type );
		if(nodeInfo!=null)
			sb.append(", nodeInfo=" + nodeInfo.toString());
		if(joinpointInfo!=null)
			sb.append(", joinpointInfo=" + joinpointInfo.toString() );
		if(commInfo!=null)
			sb.append(", commInfo=" + commInfo.toString());
		if(callInfo!=null)
			sb.append(", callInfo=" + callInfo.toString());
		if(exceptionInfo!=null)
			sb.append(", exceptionInfo=" + exceptionInfo.toString());
		if(telemetryInfo!=null)
			sb.append(", telemetryInfo=" + telemetryInfo.toString());
		sb.append(", regions="
				+ Arrays.toString(regions) + "]");
		return sb.toString();
//		return "TracePacket [type=" + type + ", nodeInfo=" + nodeInfo.toString()
//				+ ", joinpointInfo=" + joinpointInfo.toString() + ", commInfo=" + commInfo.toString()
//				+ ", callInfo=" + callInfo.toString() + ", exceptionInfo=" + exceptionInfo.toString()
//				+ ", telemetryInfo=" + telemetryInfo.toString() + ", regions="
//				+ Arrays.toString(regions) + "]";
	}
}
