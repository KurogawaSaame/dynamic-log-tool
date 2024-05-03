package multitier_log_agent.log_server.output;

import java.util.Map;

import multitier_log_agent.log_server.model.Event;
import multitier_log_agent.log_server.model.StreamWorker;
import multitier_log_agent.log_server.util.signals.Action0;
import multitier_log_agent.log_server.util.signals.Action1;
import multitier_log_agent.log_server.util.signals.SignalRegRecorder;

public abstract class AbstractOutput {
    
    private SignalRegRecorder subscriptions = new SignalRegRecorder();
    
    public void register(StreamWorker stream) {
        subscriptions.register(stream.SignalNewCase, new Action1<Integer>() {
            @Override
            public void call(Integer t) {
                newCase(t);
            }
        });
        subscriptions.register(stream.SignalNewEvent, new Action1<Event>() {
            @Override
            public void call(Event t) {
                newEvent(t);
            }
        });
        subscriptions.register(stream.SignalCaseFinished, new Action1<Integer>() {
            @Override
            public void call(Integer t) {
                caseFinished(t);
            }
        });
        subscriptions.register(stream.SignalFinish, new Action0() {
            @Override
            public void call() {
                finish();
            }
        });
    }

    public void unregisterAll() {
        subscriptions.unregisterAll();
    }
    
    /**
     * Case with given id is created
     */
    protected abstract void newCase(Integer t);

    /**
     * New event is to be available
     */
    protected abstract void newEvent(Event t);

    /**
     * Case with given id is finished / closed / flushed
     */
    protected abstract void caseFinished(Integer t);

    /**
     * Shutdown signal, all cases should be closed / flushed
     */
    protected abstract void finish();


    protected String _get(Map<String, String> attribs, String key, String def) {
        String val = attribs.get(key);
        if (val == null) {
            val = def;
        }
        return val;
    }
}
