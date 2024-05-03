package multitier_log_agent.log_server.util.signals;

import java.util.ArrayList;
import java.util.List;

/**
 * Signal Registration Recorder
 * 
 * @author mleemans
 *
 *         Container recording a group of registrations, allowing easy
 *         registration management: quickly unregistering all listeners in this
 *         group.
 */
public class SignalRegRecorder {

    @SuppressWarnings("rawtypes")
    private List<SignalRegistration> registrations = new ArrayList<>();

    public <T> void register(AbstractSignal<T> signal, T listener) {
        registrations.add(signal.register(listener));
    }

    @SuppressWarnings("rawtypes")
    public void unregisterAll() {
        for (SignalRegistration reg : registrations) {
            reg.unregister();
        }
        registrations.clear();
    }

}
