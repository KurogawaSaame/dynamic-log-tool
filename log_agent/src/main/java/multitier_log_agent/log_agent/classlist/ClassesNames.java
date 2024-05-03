package multitier_log_agent.log_agent.classlist;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassesNames {
    public static boolean isprinted=false;

    public static void printClassesInfo(){
        if (!isprinted){
            for(String clazz:ClazzGainner.savedClasses) System.out.println("收集到的类信息——————————————————————————："+clazz);
            /*try {
                ConstantReader.printClassInfoToJson(classes);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Some errors here when finding class by name!");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Some errors here when writring file!");
            }*/
            isprinted=true;
        }
    }
}
