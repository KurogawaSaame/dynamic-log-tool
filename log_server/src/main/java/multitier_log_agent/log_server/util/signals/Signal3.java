package multitier_log_agent.log_server.util.signals;

public class Signal3<T, U, V> extends AbstractSignal<Action3<T, U, V>> {

    public void dispatch(T t, U u, V v) {
        final int size = listeners.size();
        for (int i = 0; i < size; i++) {
            listeners.get(i).call(t, u, v);
        }
    }
    
}
