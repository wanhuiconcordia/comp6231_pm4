package tools.message;
import tools.channel.ChannelManager;

public interface MessageProcesser {
	public abstract void dispatchMessage(ChannelManager channelManager, Message msg);
	public abstract void processMessage(ChannelManager channelManager, Message msg);
}
