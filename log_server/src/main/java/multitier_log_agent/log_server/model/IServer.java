package multitier_log_agent.log_server.model;

import multitier_log_agent.log_server.util.signals.Signal1;

public interface IServer extends Runnable {

    public void requestStop();
    
    public Signal1<ServerStatus> SignalStatus();
    
}
