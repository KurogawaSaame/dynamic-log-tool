package multitier_log_agent.log_server.util.signals;

public class Signal0 extends AbstractSignal<Action0> {

    public void dispatch() {
        final int size = listeners.size();
        for (int i = 0; i < size; i++) {
            listeners.get(i).call();
        }
    }
    
}
