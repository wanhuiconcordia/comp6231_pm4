package tools.message;

public interface MessageProcesser {
	public void processMessage(Message msg);
	public void dispatchMessage(Message msg);
}
