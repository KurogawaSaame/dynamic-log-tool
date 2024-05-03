package multitier_log_agent.log_agent.transform.instrumenter;

import java.util.List;
import java.util.regex.Pattern;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.NotFoundException;
import javassist.expr.ConstructorCall;
import javassist.expr.Expr;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;
import multitier_log_agent.log_agent.client.LogClient;
import multitier_log_agent.log_agent.client.LogClientCodegen;
import multitier_log_agent.log_agent.transform.PointcutUtil;
import multitier_log_agent.log_agent.transform.rule.ThreadCallPointcutRule;
import multitier_log_agent.log_agent.transform.rule.MethodPointcutRule;
import multitier_log_agent.log_shared.model.JoinpointInfo.EventType;

public class CallInstrumenter extends CatchInstrumenter {

    private List<Pattern> includeCalls;
    private boolean traceConstructor;

    public CallInstrumenter(CtBehavior implMethod, MethodPointcutRule rule) {
        super(implMethod, rule);
        this.traceConstructor = rule.isTraceConstructor();

        if (rule instanceof ThreadCallPointcutRule) {
            includeCalls = ((ThreadCallPointcutRule) rule).getIncludeCalls();
        } else {
            logger.error("rule should be instanceof MethodCallPointcutRule");
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void edit(NewExpr e) throws CannotCompileException {
        try {
//            logger.debug("\tCheck on new '" + e.getConstructor().getLongName() + "'");
            _instrument(
                e, e.getConstructor().getLongName(),
                "$_ = $proceed($$)", "$_",
                EventType.CALL_NEW, EventType.RETURN_NEW,
                rule.isTraceParams(), rule.isTraceParams()
            );
        } catch (NotFoundException ex) {
            logger.error("Could not find constructor method");
        }
    }

    @Override
    public void edit(ConstructorCall c) throws CannotCompileException {
        if (traceConstructor) {
            try {
//                logger.debug("\tCheck on constructor '" + c.getMethodName() + "'");
                _instrument(
                    c, c.getConstructor().getLongName(),
                    "$proceed($$)", "$0",
                    null, EventType.RETURN_NEW,
                    rule.isTraceParams(), false
                );
            } catch (NotFoundException e) {
                logger.error("Could not find constructor method");
            }
        }
    }

    @Override
    public void edit(MethodCall m) throws CannotCompileException {
        try {
//            logger.debug("\tCheck on call '" + m.getMethod().getLongName() + "'");
            _instrument(
                m, m.getMethod().getLongName(),
                "$_ = $proceed($$)", "$0",
                EventType.CALL, EventType.RETURN,
                rule.isTraceParams(), rule.isTraceParams()
            );
        } catch (NotFoundException e) {
            logger.error("Could not find callee method");
        }
    }

//    @Override
//    public void edit(Handler m) throws CannotCompileException {
//        String longName = m.where().getLongName();
//        Agent.logger.debug("\tCheck on handle '" + longName + "'");
//        _instrument(
//            m, longName,
//            null, "$1",
//            "recordHandle", null
//        );
//    }

    //zhc注释
//    private void _instrument(Expr e, String longName,
//            String proceedStr, String targetRef,
//            EventType eventTypeBefore, EventType eventTypeAfter,
//            boolean includeParams, boolean includeReturn) throws CannotCompileException {
//        // CtMethod calleeMethod = m.getMethod();
//        // check if we want to trace this call
//        //Agent.logger.debug("\tCheck on expression '" + longName + "'");
//        if ((!longName.startsWith(LogClient.class.getName())
//            && PointcutUtil.patternsAccept(includeCalls, longName))) {
//            logger.debug("\tInstrument on expr '" + longName + "'");
//
//            if (eventTypeBefore != null) {
//                String recordEntry = LogClientCodegen.recordCallJoinpoint(
//                        e, longName, targetRef, implMethod, eventTypeBefore,
//                        rule.getRegionJavaString(), includeParams, false);
//                String recordExit = LogClientCodegen.recordCallJoinpoint(
//                        e, longName, targetRef, implMethod, eventTypeAfter,
//                        rule.getRegionJavaString(), false, includeReturn);
//                e.replace("{ " + recordEntry + "; " + proceedStr + "; " + recordExit + " }");
//            } else {
//                String recordExit = LogClientCodegen.recordCallJoinpoint(
//                        e, longName, targetRef, implMethod, eventTypeAfter,
//                        rule.getRegionJavaString(), false, includeReturn);
//                e.replace("{ " + proceedStr + "; " + recordExit + " }");
//            }
//        }
//    }

    private void _instrument(Expr e, String longName,
                             String proceedStr, String targetRef,
                             EventType eventTypeBefore, EventType eventTypeAfter,
                             boolean includeParams, boolean includeReturn) throws CannotCompileException {
        // CtMethod calleeMethod = m.getMethod();
        // check if we want to trace this call
        //Agent.logger.debug("\tCheck on expression '" + longName + "'");
        //System.out.println(123456);
        if (!longName.startsWith(LogClient.class.getName())
                && PointcutUtil.patternsAccept(longName)) {
            //System.out.println("call name:" + longName);
            logger.debug("\tInstrument on expr '" + longName + "'");
            String recordEntry = LogClientCodegen.recordCallJoinpoint(
                        e, longName, targetRef, implMethod, EventType.CALL_THREAD,
                        rule.getRegionJavaString(), true, true);
            String recordExit = LogClientCodegen.recordCallJoinpoint(
                        e, longName, targetRef, implMethod, EventType.RETURN_THREAD,
                        rule.getRegionJavaString(), true, true);
            e.replace("{ " + recordEntry + "; " + proceedStr + "; " + recordExit + " }");
            //System.out.println(recordEntry);
        }
    }
}
