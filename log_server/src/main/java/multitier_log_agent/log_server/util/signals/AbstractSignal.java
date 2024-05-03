package multitier_log_agent.log_server.util.signals;

import java.util.ArrayList;
import java.util.List;

public class AbstractSignal<T> {
    
    protected final List<T> listeners = new ArrayList<>();

    public SignalRegistration<T> register(T listener) {
        listeners.add(listener);
        return new SignalRegistration<T>(this, listener);
    }
    
    public void unregister(T listener) {
        listeners.remove(listener);
    }
}
