package multitier_log_agent.log_shared.model;

import java.util.List;

public class Method {
    private int modifier;  //方法修饰符
    private String type;   //方法返回类型
    private String name;   //方法名称,简单名称main
    private String[] paramType; //方法参数的返回类型列表

    public Method(){}

    public Method(int modifier, String type, String name, String[] paramType){
        this.modifier = modifier;
        this.type = type;
        this.name = name;
        this.paramType = paramType;
    }

    public Method(int modifier, String name, String[] paramType){
        this.modifier = modifier;
        this.name = name;
        this.paramType = paramType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getModifier() {
        return modifier;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getParamType() {
        return paramType;
    }

    public void setParamType(String[] paramType) {
        this.paramType = paramType;
    }
}
