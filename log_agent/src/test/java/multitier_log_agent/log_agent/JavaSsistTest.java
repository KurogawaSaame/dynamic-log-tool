package multitier_log_agent.log_agent;

import javassist.*;
import org.junit.Test;

public class JavaSsistTest {
    @Test
    public void getReturnType() throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get("multitier_log_agent.log_agent.util.MethodVisitor");
        CtBehavior[] ctBehaviors=cc.getDeclaredMethods();
        for (CtBehavior ctBehavior:ctBehaviors){
            CtMethod cm=(CtMethod)ctBehavior;
            String bname=ctBehavior.getName();
            CtMethod ctm = cc.getDeclaredMethod(bname);
            CtClass returnType=cm.getReturnType();
            System.out.println(returnType.getName());
        }
    }
}
