/*
package multitier_log_agent.log_agent;

import javassist.*;
import multitier_log_agent.log_agent.client.LogClientCodegen;
import multitier_log_agent.log_agent.transform.rule.MethodPointcutRule;
import multitier_log_agent.log_shared.model.JoinpointInfo;
import multitier_log_agent.log_shared.model.TracePacket;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class LogClientCodegenTest  {
    @Test
    public void recordJoinpointTest() throws NotFoundException, IOException {
        ClassPool cp = ClassPool.getDefault();
        CtClass cc=cp.get("multitier_log_agent.log_shared.model.ReturnData");
        CtMethod cm=cc.getDeclaredMethod("getType");
        String targetRef = "this";
        StringBuilder result = new StringBuilder();
        MethodPointcutRule rule=new MethodPointcutRule();
        String resu= LogClientCodegen.recordJoinpoint3(cc,cm,targetRef, JoinpointInfo.EventType.CALL, rule.getRegionJavaString(),false,false);
        System.out.println(resu);
        {
            TracePacket packe=multitier_log_agent.log_agent.client.LogClient.recordJoinpoint_Test(
                new multitier_log_agent.log_shared.model.JoinpointInfo
                        ("multitier_log_agent.log_shared.model.ReturnData",
                         "multitier_log_agent.log_shared.model.ReturnData.getType()",
                         "ReturnData.java",
                         26,System.identityHashCode(this),System.currentTimeMillis(),
                          System.nanoTime(),multitier_log_agent.log_shared.model.JoinpointInfo.EventType.CALL,
                          Thread.currentThread().getId(),
                          new multitier_log_agent.log_shared.model.Argument[0]),new java.lang.String[0]);

        }
    }

    public void chuanshuTest(){

    }
}
*/
