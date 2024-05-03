package multitier_log_agent.log_agent.transform.instrumenter;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.Modifier;
import javassist.expr.ExprEditor;
import javassist.expr.Handler;
import multitier_log_agent.log_agent.client.LogClientCodegen;
import multitier_log_agent.log_agent.transform.rule.MethodPointcutRule;
import multitier_log_agent.log_shared.model.JoinpointInfo.EventType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class
CatchInstrumenter extends ExprEditor {

    public static Logger logger = LogManager.getLogger(CatchInstrumenter.class);
    
    protected final CtBehavior implMethod;
    protected final MethodPointcutRule rule;
    
    protected final boolean traceCatch;
    
    public CatchInstrumenter(CtBehavior implMethod, MethodPointcutRule rule) {
        this.implMethod = implMethod;
        this.rule = rule;
        this.traceCatch = rule.isTraceCatch();
    }

    public void edit(Handler h) throws CannotCompileException {
        if (traceCatch && !h.isFinally()) {
            logger.debug("\tInstrument handle on line '" + h.getLineNumber() + "'");

            String targetRef = "this";
            if (Modifier.isStatic(h.where().getModifiers())) {
                targetRef = null;
            }
            
//            String code = "";
//            try {
            if (h.isFinally()) {
                // finally block
//                    code = LogClientCodegen.recordFinallyHandleJoinpoint(
//                            h, h.where().getLongName(), targetRef, EventType.HANDLE, 
//                            rule.getRegionJavaString());
//                    h.insertBefore(code);
//                h.insertBefore("{}"); // nop
//                h.insertBefore(LogClientCodegen.recordFinallyHandleJoinpoint(
//                        h, h.where().getLongName(), targetRef, EventType.HANDLE, 
//                        rule.getRegionJavaString()));
                h.insertBefore("{ Thread.activeCount(); }"); // nop
            } else {
                // catch block
//                    code = LogClientCodegen.recordExceptionHandleJoinpoint(
//                            h, h.where().getLongName(), targetRef, EventType.HANDLE, 
//                            rule.getRegionJavaString());
//                    h.insertBefore(code);
                h.insertBefore(LogClientCodegen.recordExceptionHandleJoinpoint(
                        h, h.where().getLongName(), targetRef, EventType.HANDLE, 
                        rule.getRegionJavaString()));
            }
            //} catch (NotFoundException | NullPointerException e) {
//            } catch (NotFoundException e) {
//                logger.error(e);
//                logger.debug(" --- Code: ---\n" + code);
//            }
        }
    }
}
