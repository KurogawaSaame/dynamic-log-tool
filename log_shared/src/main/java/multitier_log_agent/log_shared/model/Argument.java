package multitier_log_agent.log_shared.model;

public class Argument {
    private String type; //参数类型
    private String value; //参数值

    public Argument() {
    }
//todo:
    public Argument(String type, String value) {
        if (type.equals("null")){
            this.type = "null";
            this.value = "null";
        }
        else if(value.equals("null")){
            this.type = type;
            this.value = "null";
        }
        else {
            this.type = type;
            this.value = value;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
