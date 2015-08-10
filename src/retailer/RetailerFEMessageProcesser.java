package retailer;

import tools.channel.ChannelManager;
import tools.message.Message;
import tools.message.MessageProcesser;

public class RetailerFEMessageProcesser implements MessageProcesser {
	public ChannelManager channelManager;
	
	public RetailerFEMessageProcesser(ChannelManager channelManager){
		this.channelManager = channelManager;
	}

	@Override
	public void processMessage(Message msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispatchMessage(Message msg) {
		// TODO Auto-generated method stub

	}

}
