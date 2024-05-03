package multitier_log_agent.log_server.util.signals;

public class Signal1<T> extends AbstractSignal<Action1<T>> {

    public void dispatch(T t) {
        final int size = listeners.size();
        for (int i = 0; i < size; i++) {
            listeners.get(i).call(t);
        }
    }
    
}
