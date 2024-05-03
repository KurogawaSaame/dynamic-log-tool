package multitier_log_agent.log_server.util.signals;

public interface Action1<T> {
	void call(T t);
}
