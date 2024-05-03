package multitier_log_agent.log_server.util.signals;

public interface Action3<T, U, V> {
	void call(T t, U u, V v);
}