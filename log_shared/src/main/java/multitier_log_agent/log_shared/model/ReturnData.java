package multitier_log_agent.log_shared.model;

public class ReturnData {
    private String type;
    private String value;

    public ReturnData() {
    }
//TODO: 如果没有value就让value=null，并且以后要考虑一下对象类型的返回值
    public ReturnData(String type) {
        this.type = type;
        this.value="null";
    }

    public ReturnData(String type, Object value) {
        this.type = type;
        if(value==null){
            this.value="null";
        }
        else{
            this.value=value.toString();
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
