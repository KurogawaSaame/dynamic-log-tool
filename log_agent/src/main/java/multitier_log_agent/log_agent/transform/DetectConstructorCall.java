package multitier_log_agent.log_agent.transform;

import javassist.CannotCompileException;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

public class DetectConstructorCall extends ExprEditor {

    private boolean detectedConstructorCall;

    /**
     * Edits a constructor call (overridable).
     * The constructor call is either
     * <code>super()</code> or <code>this()</code>
     * included in a constructor body.
     *
     * The default implementation performs nothing.
     *
     * @see #edit(NewExpr)
     */
    public void edit(ConstructorCall c) throws CannotCompileException {
        detectedConstructorCall = true;
    }

    public void resetDetection() {
        detectedConstructorCall = false;
    }

    public boolean detectedConstructorCall() {
        return detectedConstructorCall;
    }
}
