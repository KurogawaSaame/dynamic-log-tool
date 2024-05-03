package multitier_log_agent.log_agent.classlist;

import multitier_log_agent.log_agent.transform.PointcutUtil;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class ClazzGainner {
    public static Set<String> savedClasses=new HashSet<>();

    public static void getMoreClassesByName(String class_name,List<Pattern>include){
        JavaClass clazz = null;
        try {
            clazz = Repository.lookupClass(class_name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        ConstantPool pool=clazz.getConstantPool();
        Constant[] constants=pool.getConstantPool();
        for(Constant constant:constants){
            if(constant!=null&&constant.getTag()==7){
                String name=pool.constantToString(constant);
                if(!savedClasses.contains(name)&&PointcutUtil.patternsAccept(include, name)){
                    savedClasses.add(name);
                    getMoreClassesByName(name,include);
                }
            }
        }
    }
}
