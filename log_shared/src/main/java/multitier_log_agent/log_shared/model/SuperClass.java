package multitier_log_agent.log_shared.model;

public class SuperClass {
    private String className; //包+类名
    private Attribute[] attributes;
    private Method[] constructionMethods;
    private Method[] methods;

    public SuperClass(String className, Attribute[] attributes, Method[] constructionMethods, Method[] methods) {
        this.className = className;
        this.attributes = attributes;
        this.constructionMethods = constructionMethods;
        this.methods = methods;
    }

    public SuperClass(String className, Attribute[] attributes, Method[] methods) {
        this.className = className;
        this.attributes = attributes;
        this.methods = methods;
    }

    public SuperClass(){

    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Attribute[] getAttributes() {
        return attributes;
    }

    public void setAttributes(Attribute[] attributes) {
        this.attributes = attributes;
    }

    public Method[] getConstructionMethods() {
        return constructionMethods;
    }

    public void setConstructionMethods(Method[] constructionMethods) {
        this.constructionMethods = constructionMethods;
    }

    public Method[] getMethods() {
        return methods;
    }

    public void setMethods(Method[] methods) {
        this.methods = methods;
    }
}
