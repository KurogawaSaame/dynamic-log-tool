package multitier_log_agent.log_agent.util;

import multitier_log_agent.log_agent.transform.PointcutUtil;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.*;

import java.util.*;
import java.util.regex.Pattern;

public class MethodVisitor extends EmptyVisitor {
    JavaClass visitedClass;
    private MethodGen mg;
    private ConstantPoolGen cp;
    private Set<String> fieldCalls = new HashSet<>();
    protected List<Pattern> include;

    public MethodVisitor(MethodGen m, JavaClass jc,List<Pattern> include) {
        visitedClass = jc;
        mg = m;
        cp = mg.getConstantPool();
        this.include=include;
    }
    public Set<String> startField() {
        if (mg.isAbstract() || mg.isNative())
            return Collections.emptySet();

        for (InstructionHandle ih = mg.getInstructionList().getStart();
             ih != null; ih = ih.getNext()) {
            Instruction i = ih.getInstruction();

            if (!visitInstruction(i))
                i.accept(this);
        }
        return fieldCalls;
    }
    private boolean visitInstruction(Instruction i) {
        short opcode = i.getOpcode();
        return ((InstructionConst.getInstruction(opcode) != null)
                && !(i instanceof ConstantPushInstruction)
                && !(i instanceof ReturnInstruction));
    }
    @Override
    public void visitFieldInstruction(FieldInstruction obj) {
        if(!PointcutUtil.patternsAccept(include,obj.getReferenceType(cp).toString())) return;
        fieldCalls.add(obj.getReferenceType(cp)+"."+obj.getFieldName(cp));
    }

}

