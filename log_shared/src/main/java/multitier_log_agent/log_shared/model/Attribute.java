package multitier_log_agent.log_shared.model;

public class Attribute {
    private int modifier;
    private String type;
    private String name;

    public Attribute(int modifier, String type, String name){
        this.modifier = modifier;
        this.type = type;
        this.name = name;
    }

    public Attribute(){

    }

    public int getModifier() {
        return modifier;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }
}
