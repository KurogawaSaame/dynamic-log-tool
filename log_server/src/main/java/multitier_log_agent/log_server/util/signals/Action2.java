package multitier_log_agent.log_server.util.signals;

public interface Action2<T, U> {
	void call(T t, U u);
}
