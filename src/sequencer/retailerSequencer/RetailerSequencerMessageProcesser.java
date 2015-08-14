package sequencer.retailerSequencer;

import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.message.AckMessage;
import tools.message.Action;
import tools.message.Message;
import tools.message.MessageProcesser;
import tools.message.Packet;
import tools.message.retailerFE.RetailerFEGetCatelogMessage;
import tools.message.retailerFE.RetailerFESignInMessage;
import tools.message.retailerFE.RetailerFESignUpMessage;
import tools.message.retailerFE.RetailerFESubmitOrderMessage;
import tools.message.retailerSequencer.RetailerSequencerGetCatelogMessage;
import tools.message.retailerSequencer.RetailerSequencerSignInMessage;
import tools.message.retailerSequencer.RetailerSequencerSignUpMessage;
import tools.message.retailerSequencer.RetailerSequencerSubmitOrderMessage;

public class RetailerSequencerMessageProcesser extends MessageProcesser {

	@Override
	public void processTimeout(ChannelManager channelManager){
		for(Channel channel: channelManager.channelMap.values()){
			if(channel.isWaitingForRespose){
				channelManager.outgoingPacketQueue.add(channel.backupPacket);
			}
		}
	}

	@Override
	public void processNewRequest(ChannelManager channelManager, Channel channel, Message msg) {
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
				for(Channel replicaChannel: channelManager.channelMap.values()){
					if(replicaChannel.group == Group.REPLICA){
						channel.backupPacket = new Packet(channel.peerHost
								, channel.peerPort
								, generateRetailerSequencerMessage(replicaChannel.localProcessName
										, ++replicaChannel.localSeq
										, replicaChannel.peerSeq
										, channelManager.sequencerID
										, msg)); 
						replicaChannel.isWaitingForRespose = true;
						synchronized(channelManager.outgoingPacketQueueLock) {
							channelManager.outgoingPacketQueue.add(replicaChannel.backupPacket);
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
					, ((RetailerFEGetCatelogMessage)receivedMsg).customerReferenceNumber
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
