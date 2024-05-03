package multitier_log_agent.log_server.util.signals;

public class Signal2<T, U> extends AbstractSignal<Action2<T, U>> {

    public void dispatch(T t, U u) {
        final int size = listeners.size();
        for (int i = 0; i < size; i++) {
            listeners.get(i).call(t, u);
        }
    }
    
}
