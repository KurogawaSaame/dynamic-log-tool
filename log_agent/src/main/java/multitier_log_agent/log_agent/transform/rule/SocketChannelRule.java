package multitier_log_agent.log_agent.transform.rule;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import multitier_log_agent.log_agent.client.LogClientCodegen;
import multitier_log_agent.log_agent.query.IClassQuery;
import multitier_log_agent.log_agent.query.IMethodQuery;
import multitier_log_agent.log_agent.transform.AbstractTransformationRule;
import multitier_log_agent.log_agent.transform.TransformationRuleOrder;
import multitier_log_agent.log_shared.model.JoinpointInfo.EventType;

import org.apache.commons.configuration.HierarchicalConfiguration;

public class SocketChannelRule extends AbstractTransformationRule {

    public static final String Name = "SocketChannelRule";

    private static final String TARGET_CLASS = "sun.nio.ch.SocketChannelImpl";
    
    private static final String In_Method_Name = "read";
    private static final String In_Method_Desc[] = new String[] { 
        "([Ljava/nio/ByteBuffer;)J",
        "(Ljava/nio/ByteBuffer;)I",
        "([Ljava/nio/ByteBuffer;II)J"
    };
    private static final String In_Method_FullName[] = new String[] {
        TARGET_CLASS + "." + In_Method_Name + "(java.nio.ByteBuffer)",
        TARGET_CLASS + "." + In_Method_Name + "(java.nio.ByteBuffer[])",
        TARGET_CLASS + "." + In_Method_Name + "(java.nio.ByteBuffer[],int,int)",
    };

    private static final String Out_Method_Name = "write";
    private static final String Out_Method_Desc[] = new String[] { 
        "([Ljava/nio/ByteBuffer;II)J",
        "([Ljava/nio/ByteBuffer;)J",
        "(Ljava/nio/ByteBuffer;)I"
    };
    private static final String Out_Method_FullName[] = new String[] {
        TARGET_CLASS + "." + Out_Method_Name + "(java.nio.ByteBuffer)",
        TARGET_CLASS + "." + Out_Method_Name + "(java.nio.ByteBuffer[])",
        TARGET_CLASS + "." + Out_Method_Name + "(java.nio.ByteBuffer[],int,int)",
    };
    
    public SocketChannelRule() {
        super(Name, TransformationRuleOrder.SocketChannel.ordinal());
    }

    public SocketChannelRule(HierarchicalConfiguration config) {
        this();
        logger.info("\tConfigure rule: " + getName());
        
        for (HierarchicalConfiguration region : config.configurationsAt("region")) {
            addRegion(region.getString(""));
        }
    }
    
    @Override
    public boolean isClassIncluded(IClassQuery query) {
        String fullDotName = query.getFullDotName();
        return TARGET_CLASS.equals(fullDotName);
    }

    @Override
    public boolean isMethodIncluded(IMethodQuery query) {
        String fullDotName = query.getFullDotName();
        boolean res = In_Method_FullName.equals(fullDotName);
        for (String fullName : Out_Method_FullName) {
            res = res || fullName.equals(fullDotName);
        }
        return res;
    }

    @Override
    public boolean transform(CtClass cc) throws NotFoundException,
            CannotCompileException {
        if (TARGET_CLASS.equals(cc.getName())) {
            // read-write hooks
            for (int i = 0; i < In_Method_Desc.length; i++) {
                transform(cc.getMethod(In_Method_Name, In_Method_Desc[i]));
            }
            for (int i = 0; i < Out_Method_Desc.length; i++) {
                transform(cc.getMethod(Out_Method_Name, Out_Method_Desc[i]));
            }
            return true;
        }
        return false;
    }

    private void transform(CtMethod m) throws CannotCompileException {
        //System.out.println("\t\tModify: " + method.getName() + " : "
        //      + method.getSignature());

        m.insertBefore(LogClientCodegen.recordCommJoinpointSocket(
                m, EventType.CALL, "this.socket()", getRegionJavaString()));
        m.insertAfter(LogClientCodegen.recordCommJoinpointSocket(
                m, EventType.RETURN, "this.socket()", getRegionJavaString()));
    }
}
