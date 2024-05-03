package multitier_log_agent.log_shared.model;

public class ThreadInfo {
	private long parentThreadId;
	private long childThreadId;
	public ThreadInfo(){}
	public ThreadInfo(long parentThreadId, long childThreadId) {
		super();
		this.parentThreadId = parentThreadId;
		this.childThreadId = childThreadId;
	}
	public long getParentThreadId() {
		return parentThreadId;
	}
	public void setParentThreadId(long parentThreadId) {
		this.parentThreadId = parentThreadId;
	}
	public long getChildThreadId() {
		return childThreadId;
	}
	public void setChildThreadId(long childThreadId) {
		this.childThreadId = childThreadId;
	}
	
}
