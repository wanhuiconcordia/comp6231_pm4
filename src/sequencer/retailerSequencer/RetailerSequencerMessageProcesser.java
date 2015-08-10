package sequencer.retailerSequencer;

import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.message.AckMessage;
import tools.message.Message;
import tools.message.MessageProcesser;
import tools.message.RetailerFESignInMessage;
import tools.message.RetailerFESignUpMessage;
import tools.message.RetailerSequencerGetCatelogMessage;
import tools.message.RetailerSequencerSignInMessage;
import tools.message.RetailerSequencerSignUpMessage;

public class RetailerSequencerMessageProcesser implements MessageProcesser {

	public ChannelManager channelManager;
	public 	int sequencerID;

	public RetailerSequencerMessageProcesser(ChannelManager channelManager){
		this.channelManager = channelManager;
		sequencerID = 0;
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
					thisChannel.localSeq = 0;
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
		switch(msg.action){
		case getCatelog:
			sequencerID++;
			for(Channel channel: channelManager.channelMap.values()){
				if(channel.group == Group.RetailerReplica){
					channel.cachedMsg = new RetailerSequencerGetCatelogMessage(channel.localProcessName
							, ++channel.localSeq
							, channel.peerSeq
							, sequencerID); 
					channel.hasCachedMsg = true;		
				}
			}
			break;
		case signIn:
			sequencerID++;
			for(Channel channel: channelManager.channelMap.values()){
				if(channel.group == Group.RetailerReplica){
					channel.cachedMsg = new RetailerSequencerSignInMessage(channel.localProcessName
							, ++channel.localSeq
							, channel.peerSeq 
							, ((RetailerFESignInMessage)msg).customerReferenceNumber 
							, ((RetailerFESignInMessage)msg).password, sequencerID);
					channel.hasCachedMsg = true;		
				}
			}			
			break;
		case signUp:
			sequencerID++;
			for(Channel channel: channelManager.channelMap.values()){
				if(channel.group == Group.RetailerReplica){
					channel.cachedMsg = new RetailerSequencerSignUpMessage(channel.localProcessName
							, ++channel.localSeq
							, channel.peerSeq
							, ((RetailerFESignUpMessage)msg).name
							, ((RetailerFESignUpMessage)msg).password
							, ((RetailerFESignUpMessage)msg).street1
							, ((RetailerFESignUpMessage)msg).street2
							, ((RetailerFESignUpMessage)msg).city
							, ((RetailerFESignUpMessage)msg).state
							, ((RetailerFESignUpMessage)msg).zip
							, ((RetailerFESignUpMessage)msg).country
							, sequencerID);
					channel.hasCachedMsg = true;		
				}
			}		
			break;
		case submitOrder:
			//			thisChannel.cachedMsg = new AckMessage(thisChannel.localProcessName, ++thisChannel.localSeq, msg.senderSeq);
			//			thisChannel.hasCachedMsg = true;
			dispatchMessage(msg);
			break;
		default:
			System.out.println("Unrecognizable action");
			break;
		}
	}
}
