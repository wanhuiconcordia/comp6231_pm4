package sequencer.retailerSequencer;

import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.message.AckMessage;
import tools.message.Message;
import tools.message.MessageProcesser;
import tools.message.Packet;
import tools.message.RetailerFESignInMessage;
import tools.message.RetailerFESignUpMessage;
import tools.message.RetailerFESubmitOrderMessage;
import tools.message.RetailerSequencerGetCatelogMessage;
import tools.message.RetailerSequencerSignInMessage;
import tools.message.RetailerSequencerSignUpMessage;
import tools.message.RetailerSequencerSubmitOrderMessage;

public class RetailerSequencerMessageProcesser implements MessageProcesser {

	@Override
	public void processMessage(ChannelManager channelManager, Message msg) {
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
					synchronized(channelManager.outgoingPacketQueueLock) {
						channelManager.outgoingPacketQueue.add(new Packet(thisChannel.peerHost
								, thisChannel.peerPort
								, new AckMessage(thisChannel.localProcessName, ++thisChannel.localSeq, msg.senderSeq)));
					}
					break;
				case getCatelog:
				case signIn:
				case signUp:
				case submitOrder:
					synchronized(channelManager.outgoingPacketQueueLock) {
						channelManager.outgoingPacketQueue.add(new Packet(thisChannel.peerHost
								, thisChannel.peerPort
								, new AckMessage(thisChannel.localProcessName, ++thisChannel.localSeq, msg.senderSeq)));
					}
					dispatchMessage(channelManager, msg);
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
	public void dispatchMessage(ChannelManager channelManager, Message msg) {
		switch(msg.action){
		case getCatelog:
			channelManager.sequencerID++;
			for(Channel channel: channelManager.channelMap.values()){
				if(channel.group == Group.RetailerReplica){
					channel.cachedMsg = new RetailerSequencerGetCatelogMessage(channel.localProcessName
							, ++channel.localSeq
							, channel.peerSeq
							, channelManager.sequencerID); 
					channel.hasCachedMsg = true;
					synchronized(channelManager.outgoingPacketQueueLock) {
						channelManager.outgoingPacketQueue.add(new Packet(channel.peerHost
								, channel.peerPort
								, channel.cachedMsg));
					}
				}
			}
			break;
		case signIn:
			channelManager.sequencerID++;
			for(Channel channel: channelManager.channelMap.values()){
				if(channel.group == Group.RetailerReplica){
					channel.cachedMsg = new RetailerSequencerSignInMessage(channel.localProcessName
							, ++channel.localSeq
							, channel.peerSeq 
							, ((RetailerFESignInMessage)msg).customerReferenceNumber 
							, ((RetailerFESignInMessage)msg).password, channelManager.sequencerID);
					channel.hasCachedMsg = true;
					synchronized(channelManager.outgoingPacketQueueLock) {
						channelManager.outgoingPacketQueue.add(new Packet(channel.peerHost
								, channel.peerPort
								, channel.cachedMsg));
					}
				}
			}			
			break;
		case signUp:
			channelManager.sequencerID++;
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
							, channelManager.sequencerID);
					channel.hasCachedMsg = true;
					synchronized(channelManager.outgoingPacketQueueLock) {
						channelManager.outgoingPacketQueue.add(new Packet(channel.peerHost
								, channel.peerPort
								, channel.cachedMsg));
					}
				}
			}		
			break;
		case submitOrder:
			channelManager.sequencerID++;
			for(Channel channel: channelManager.channelMap.values()){
				if(channel.group == Group.RetailerReplica){
					channel.cachedMsg = new RetailerSequencerSubmitOrderMessage(channel.localProcessName
							, ++channel.localSeq
							, channel.peerSeq
							, ((RetailerFESubmitOrderMessage)msg).customerReferenceNumber
							, ((RetailerFESubmitOrderMessage)msg).itemList
							, channelManager.sequencerID); 
					channel.hasCachedMsg = true;
					synchronized(channelManager.outgoingPacketQueueLock) {
						channelManager.outgoingPacketQueue.add(new Packet(channel.peerHost
								, channel.peerPort
								, channel.cachedMsg));
					}
				}
			}
			break;
		default:
			System.out.println("Unrecognizable action");
			break;
		}
	}
}
