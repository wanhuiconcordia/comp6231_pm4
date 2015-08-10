package sequencer.retailerSequencer;

import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.message.AckMessage;
import tools.message.Message;
import tools.message.MessageProcesser;

public class RetailerSequencerMessageProcesser implements MessageProcesser {

	public ChannelManager channelManager;
	
	public RetailerSequencerMessageProcesser(ChannelManager channelManager){
		this.channelManager = channelManager;
	}
	
	@Override
	public void processMessage(Message msg) {
		if(channelManager.channelMap.containsKey(msg.sender)){
			Channel thisChannel = channelManager.channelMap.get(msg.sender);
			
			if(thisChannel.verifySequence(msg)){
				thisChannel.peerSeq = msg.senderSeq;
				switch(msg.action){
				case ACK:
					thisChannel.hasCachedMsg = false;
					break;
				case INIT:
					thisChannel.cachedMsg = new AckMessage(thisChannel.localProcessName, ++thisChannel.localSeq, msg.senderSeq);
					thisChannel.hasCachedMsg = true;
					break;
				case getCatelog:
					thisChannel.cachedMsg = new AckMessage(thisChannel.localProcessName, ++thisChannel.localSeq, msg.senderSeq);
					thisChannel.hasCachedMsg = true;
					
					dispatchMessage(msg);
					break;
				case signIn:
					thisChannel.cachedMsg = new AckMessage(thisChannel.localProcessName, ++thisChannel.localSeq, msg.senderSeq);
					thisChannel.hasCachedMsg = true;
					dispatchMessage(msg);
					break;
				case signUp:
					thisChannel.cachedMsg = new AckMessage(thisChannel.localProcessName, ++thisChannel.localSeq, msg.senderSeq);
					thisChannel.hasCachedMsg = true;
					dispatchMessage(msg);
					break;
				case submitOrder:
					thisChannel.cachedMsg = new AckMessage(thisChannel.localProcessName, ++thisChannel.localSeq, msg.senderSeq);
					thisChannel.hasCachedMsg = true;
					dispatchMessage(msg);
					break;
				default:
					System.out.println("Unrecognizable action");
					break;
				}
			}
		}else{
			System.out.println("channelMap does not contian " + msg.sender);
		}
	}

	@Override
	public void dispatchMessage(Message msg) {
		// TODO Auto-generated method stub
	}
}
