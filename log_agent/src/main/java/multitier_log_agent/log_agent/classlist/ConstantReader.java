package multitier_log_agent.log_agent.classlist;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.*;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConstantReader {
    public static void readConstants() throws ClassNotFoundException {
        JavaClass clazz= Repository.lookupClass("com.maon.fupr.TestUtil");
        Field[]attributes=clazz.getFields();
        ConstantPool calzzpool=clazz.getConstantPool();
        Constant f1=calzzpool.getConstantPool()[attributes[1].getNameIndex()];
        String name=calzzpool.constantToString(f1);
        List<String> list=new ArrayList<String>();
        for (org.apache.bcel.classfile.Method method:clazz.getMethods()){
            ConstantPool constantPool=method.getConstantPool();
            Constant[]constants= constantPool.getConstantPool();
            for(Constant constant:constants){
                if (constant!=null&&constant.getTag()==9){
                    String c=constantPool.constantToString(constant);
//                    String name_c=constantPool.getConstantPool()[constant.]
                    list.add(c);
                }
            }
            int i=0;
        }
    }



    public static void printClassInfoToJson(List<String> classes) throws ClassNotFoundException, IOException {
        Map<String,List<String>> map=getFiledsOfClass(classes);
        Map<String,Map<String,List<String>>> clazz_method_constant=getMethodUsedConstants(classes);
        Map<String,Map<String,List<String>>> method_used=getMethodUsed(classes);
        writeToFile("./test.json",map,clazz_method_constant,method_used);
    }

    private static  Map<String,List<String>> getFiledsOfClass(List<String> classes_name) throws ClassNotFoundException {
        Map<String,List<String>> class_fields=new HashMap<>();
        for (String clazz_name:classes_name){
            JavaClass clazz = Repository.lookupClass(clazz_name);
            List<String>fileds_str = new ArrayList<>();
            Field[] fields = clazz.getFields();
            ConstantPool calzzpool=clazz.getConstantPool();
            for(Field field:fields){
                Constant field_constant=calzzpool.getConstantPool()[field.getNameIndex()];
                String field_name=clazz_name+"."+calzzpool.constantToString(field_constant);
                fileds_str.add(field_name);
            }
            class_fields.put(clazz_name,fileds_str);
        }
        return class_fields;
    }

    private static Map<String,Map<String,List<String>>> getMethodUsedConstants(List<String> classes_name) throws ClassNotFoundException {
        Map<String,Map<String,List<String>>>clazz_method_constant=new HashMap<>();
        for(String clazz_name:classes_name){
            Map<String,List<String>> methods_constant=new HashMap<>();
            JavaClass clazz = Repository.lookupClass(clazz_name);
            for (org.apache.bcel.classfile.Method method:clazz.getMethods()){
                List<String>uesd_constants=new ArrayList<>();
                ConstantPool constantPool=method.getConstantPool();
                Constant[]constants= constantPool.getConstantPool();
                for(Constant constant:constants){
                    if (constant!=null&&constant.getTag()==9){
                        String c=constantPool.constantToString(constant);
                        String class_and_name=c.split(" ")[0];
                        uesd_constants.add(class_and_name);
                    }
                }
                StringBuilder methodBuilder=new StringBuilder();
                Type[] types=method.getArgumentTypes();
                String mn=method.getName();
                methodBuilder.append(mn).append("(");
                String sep1="";
                for(Type type:types){
                    methodBuilder.append(sep1).append(type.toString());
                    sep1=";";
                }
                methodBuilder.append(")");
                methods_constant.put(methodBuilder.toString(),uesd_constants);
            }
            clazz_method_constant.put(clazz_name,methods_constant);
        }
        return clazz_method_constant;
    }

    private static Map<String,Map<String,List<String>>> getMethodUsed(List<String> classes_name) throws ClassNotFoundException {
        Map<String,Map<String,List<String>>>clazz_method_dependence=new HashMap<>();
        for(String clazz_name:classes_name){
            Map<String,List<String>> methods_dependence=new HashMap<>();
            JavaClass clazz = Repository.lookupClass(clazz_name);
            for (Method method:clazz.getMethods()){
                List<String>uesd_methods=new ArrayList<>();
                ConstantPool constantPool=method.getConstantPool();
                Constant[]constants= constantPool.getConstantPool();
                for(Constant constant:constants){
                    if (constant!=null&&constant.getTag()==10){//todo:方法名对应不上，改成和日志里一样
                        String classname=constantPool.constantToString(((ConstantCP)constant).getClassIndex(), (byte)7);
                        if (!classes_name.contains(classname)) continue;
                        String m=constantPool.constantToString(((ConstantCP)constant).getNameAndTypeIndex(), (byte)12);
                        String method_str=convertMethodStr(m);
                        uesd_methods.add(classname+method_str);
                    }
                }
                StringBuilder methodBuilder=new StringBuilder();
                Type[] types=method.getArgumentTypes();
                String mn=method.getName();
                if (types.length>0){
                    int aa=1;
                }
                methodBuilder.append(mn).append("(");
                String sep1="";
                for(Type type:types){
                    methodBuilder.append(sep1).append(type.toString());
                    sep1=";";
                }
                methodBuilder.append(")");
                methods_dependence.put(methodBuilder.toString(),uesd_methods);
            }
            clazz_method_dependence.put(clazz_name,methods_dependence);
        }
        return clazz_method_dependence;
    }
    private static String convertMethodStr(String method_str){
        StringBuilder res=new StringBuilder();
        String method_name=method_str.split(" ",2)[0];
        String paragram_str=method_str.split(" ",2)[1];
        String tmp=paragram_str.substring(1).split("\\)")[0];
        String return_type=paragram_str.substring(1).split("\\)")[1];
        List<String> paras=new ArrayList<>();
        if(!tmp.equals("")&&tmp.contains(";")){
            String[] tmps=tmp.split(";");
            for (String tmp_str:tmps){
                if (!tmp_str.equals("")){
                    //Ljava.lang.String;之类的字符串，按照类描述符来转换成正确的类型
                    char describle=tmp.charAt(0);
                    switch(describle){
                        case 'L':
                            paras.add(tmp_str.substring(1).replace("/","."));
                            break;
                        case 'I':
                            paras.add("int");
                            break;
                        case 'B':
                            paras.add("byte");
                            break;
                        case 'C':
                            paras.add("char");
                            break;
                        case 'D':
                            paras.add("double");
                            break;
                        case 'F':
                            paras.add("float");
                            break;
                        case 'J':
                            paras.add("long");
                            break;
                        case 'S':
                            paras.add("shot");
                            break;
                        case 'Z':
                            paras.add("boolean");
                            break;
                        case 'V':
                            break;
                    }
                }
            }
        }
        res.append(".").append(method_name).append("(");
        String sep="";
        for(String para:paras){
            res.append(sep);
            res.append(para);
            sep=";";
        }
        res.append(")");
        return res.toString();
    }

    public static void writeToFile(String filename,Map<String,List<String>> constants_of_clazz,
                                   Map<String,Map<String,List<String>>> clazz_method_constant,
                                   Map<String,Map<String,List<String>>> clazz_method_method) throws IOException {
        File file =new File(filename);
        if(!file.exists())
            file.createNewFile();
        FileOutputStream fileOutputStream=new FileOutputStream(file);
        OutputStreamWriter writer=new OutputStreamWriter(fileOutputStream,"utf-8");
        BufferedWriter bwriter=new BufferedWriter(writer);

        JSONArray classesArray=new JSONArray();
        for(Map.Entry<String,List<String>> clazz_entry:constants_of_clazz.entrySet()){
            String className=clazz_entry.getKey();
            JSONObject class_info=new JSONObject();
            class_info.put("ClassName",className);

            JSONArray fields=new JSONArray();
            for(String fields_type:clazz_entry.getValue()){
                fields.put(fields_type);
            }
            class_info.put("Fields",fields);

            JSONArray methods=new JSONArray();
            Map<String,List<String>> method_constant=clazz_method_constant.getOrDefault(className,null);
            Map<String,List<String>> method_method=clazz_method_method.getOrDefault(className,null);
            if(method_constant!=null){
                for (Map.Entry<String,List<String>> method_entry:method_constant.entrySet()){
                    JSONObject method_object=new JSONObject();
                    String method_name=method_entry.getKey();
                    method_object.put("MethodName",method_name);

                    List<String> constants=method_entry.getValue();
                    JSONArray constants_array=new JSONArray();
                    for (String constant:constants){
                        constants_array.put(constant);
                    }
                    method_object.put("UsedFields",constants_array);

                    List<String> methodsUsed=method_method.getOrDefault(method_name,null);
                    if (methodsUsed!=null){
                        JSONArray methods_array=new JSONArray();
                        for(String method:methodsUsed){
                            methods_array.put(method);
                        }
                        method_object.put("UsedMethods",methods_array);
                    }
                    else method_object.put("UsedMethods",new JSONArray());

                    methods.put(method_object);
                }
                class_info.put("Methods",methods);
            }
            else class_info.put("Methods",new JSONArray());

            classesArray.put(class_info);
        }

        bwriter.write(classesArray.toString());
        bwriter.flush();
        bwriter.close();
    }
}
