package multitier_log_agent.log_server.util.signals;

public class Signal4<T, U, V, W> extends AbstractSignal<Action4<T, U, V, W>> {

    public void dispatch(T t, U u, V v, W w) {
        final int size = listeners.size();
        for (int i = 0; i < size; i++) {
            listeners.get(i).call(t, u, v, w);
        }
    }
    
}
