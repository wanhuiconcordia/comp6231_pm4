package tools.message;
import tools.channel.ChannelManager;

public interface MessageProcesser {
	public void processMessage(ChannelManager channelManager, Message msg);
}
