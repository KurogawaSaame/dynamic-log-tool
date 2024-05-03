package multitier_log_agent.log_server.util.signals;

public interface Action4<T, U, V, W> {
	void call(T t, U u, V v, W w);
}
