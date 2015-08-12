package sequencer.retailerSequencer;

import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.message.AckMessage;
import tools.message.Action;
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
			Channel channel = channelManager.channelMap.get(msg.sender);
			if(msg.senderSeq < channel.peerSeq){
				if(msg.action == Action.INIT){
					channel.localSeq = 0;
					channel.backupPacket = new Packet(channel.peerHost
							, channel.peerPort
							, new AckMessage(channel.localProcessName
									, ++channel.localSeq
									, msg.senderSeq));
					synchronized(channelManager.outgoingPacketQueueLock) {
						channelManager.outgoingPacketQueue.add(channel.backupPacket);
					}
				}else{
					System.out.println("Delayed msg, drop...");
				}
			}else if(msg.senderSeq == channel.peerSeq){
				System.out.println("Just received msg. Respond with backupPacket.");
				synchronized(channelManager.outgoingPacketQueueLock) {
					channelManager.outgoingPacketQueue.add(channel.backupPacket);
				}
				
			}else if(msg.senderSeq == channel.peerSeq + 1){
				System.out.println("New good seq. Response and backup the packet");
				if(msg.action == Action.ACK){
					channel.isWaitingForRespose = false;
				}else{
					channel.backupPacket = new Packet(channel.peerHost
							, channel.peerPort
							, new AckMessage(channel.localProcessName
									, ++channel.localSeq
									, msg.senderSeq));
					synchronized(channelManager.outgoingPacketQueueLock) {
						channelManager.outgoingPacketQueue.add(channel.backupPacket);
					}
					
					switch(msg.action){
					case getCatelog:
					case signIn:
					case signUp:
					case submitOrder:
						channelManager.sequencerID++;
						for(Channel castChannel: channelManager.channelMap.values()){
							if(castChannel.group == Group.RetailerReplica){
								castChannel.cachedMsg = generateRetailerSequencerMessage(castChannel.localProcessName
										, ++castChannel.localSeq
										, castChannel.peerSeq
										, channelManager.sequencerID
										, msg); 
								castChannel.isWaitingForRespose = true;
								synchronized(channelManager.outgoingPacketQueueLock) {
									channelManager.outgoingPacketQueue.add(castChannel.backupPacket);
								}
							}
						}
						break;
					default:
						System.out.println("Unrecognizable action");
						break;
					}
				}
				
			}else{
				System.out.println("Messed seq.");
			}
		}else{
			System.out.println("channelMap does not contian " + msg.sender);
			channelManager.loggerClient.write("channelMap does not contian " + msg.sender);
		}
	}

	
	private Message generateRetailerSequencerMessage(String localProcessName
			, int localSeq
			, int peerSeq
			, int sequencerID
			, Message receivedMsg){
		switch(receivedMsg.action){
		case getCatelog:
					return new RetailerSequencerGetCatelogMessage(localProcessName
							, localSeq
							, peerSeq
							, sequencerID); 
		case signIn:
			return new RetailerSequencerSignInMessage(localProcessName
							, localSeq
							, peerSeq 
							, ((RetailerFESignInMessage)receivedMsg).customerReferenceNumber 
							, ((RetailerFESignInMessage)receivedMsg).password
							, sequencerID);
		case signUp:
			return new RetailerSequencerSignUpMessage(localProcessName
					, localSeq
					, peerSeq
					, ((RetailerFESignUpMessage)receivedMsg).name
					, ((RetailerFESignUpMessage)receivedMsg).password
					, ((RetailerFESignUpMessage)receivedMsg).street1
					, ((RetailerFESignUpMessage)receivedMsg).street2
					, ((RetailerFESignUpMessage)receivedMsg).city
					, ((RetailerFESignUpMessage)receivedMsg).state
					, ((RetailerFESignUpMessage)receivedMsg).zip
					, ((RetailerFESignUpMessage)receivedMsg).country
					, sequencerID);
	
		case submitOrder:
			return new RetailerSequencerSubmitOrderMessage(localProcessName
							, localSeq
							, peerSeq
							, ((RetailerFESubmitOrderMessage)receivedMsg).customerReferenceNumber
							, ((RetailerFESubmitOrderMessage)receivedMsg).itemList
							, sequencerID);
		}
		return null;
	}
}
