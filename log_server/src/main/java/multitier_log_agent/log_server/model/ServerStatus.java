package multitier_log_agent.log_server.model;

public enum ServerStatus {
    Offline("Offline"),
    Starting("Starting"),
    Running("Running"),
    Shutdown("Shutdown"),
    Error("Error");
    
    private final String msg;

    private ServerStatus(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
