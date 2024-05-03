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

public class SocketRule extends AbstractTransformationRule {

    public static final String Name = "SocketRule";

    private static final String In_Target_Class = "java.net.SocketInputStream";
    private static final String In_Method_Name = "read";
    private static final String In_Method_Desc = "([BIII)I";
    private static final String In_Method_FullName =
        In_Target_Class + "." + In_Method_Name + "(byte,int,int,int)";
    
    private static final String Out_Target_Class = "java.net.SocketOutputStream";
    private static final String Out_Method_Name = "write";
    private static final String Out_Method_Desc[] = new String[] {
        "([B)V", "([BII)V", "(I)V" 
    };
    private static final String Out_Method_FullName[] = new String[] {
        Out_Target_Class + "." + Out_Method_Name + "(byte)",
        Out_Target_Class + "." + Out_Method_Name + "(int)",
        Out_Target_Class + "." + Out_Method_Name + "(byte,int,int)",
    };
    
    public SocketRule() {
        super(Name, TransformationRuleOrder.Socket.ordinal());
    }

    public SocketRule(HierarchicalConfiguration config) {
        this();
        logger.info("\tConfigure rule: " + getName());
        
        for (HierarchicalConfiguration region : config.configurationsAt("region")) {
            addRegion(region.getString(""));
        }
    }
    
    @Override
    public boolean isClassIncluded(IClassQuery query) {
        String fullDotName = query.getFullDotName();
        return In_Target_Class.equals(fullDotName)
                || Out_Target_Class.equals(fullDotName);
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
        
        if (In_Target_Class.equals(cc.getName())) {
            transform(cc.getMethod(In_Method_Name, In_Method_Desc));
            return true;
        }else if (Out_Target_Class.equals(cc.getName())) {
            for (String desc : Out_Method_Desc) {
                transform(cc.getMethod(Out_Method_Name, desc));
            }
            return true;
        }
        
        return false;
    }

    private void transform(CtMethod m) throws CannotCompileException {
        //System.out.println("\t\tModify: " + method.getName() + " : "
        //      + method.getSignature());

        System.out.println("就是这里插装方法名" + m.getLongName());
        System.out.println(LogClientCodegen.recordCommJoinpointSocket(
                m, EventType.CALL_SOCKET, "socket", getRegionJavaString()));
        m.insertBefore(LogClientCodegen.recordCommJoinpointSocket(
                m, EventType.CALL_SOCKET, "socket", getRegionJavaString()));
        m.insertAfter(LogClientCodegen.recordCommJoinpointSocket(
                m, EventType.RETURN_SOCKET, "socket", getRegionJavaString()));
    }
}
