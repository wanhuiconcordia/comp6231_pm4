package sequencer.manufacturerSequencer;

import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.message.AckMessage;
import tools.message.Message;
import tools.message.MessageProcesser;
import tools.message.Packet;
import tools.message.manufacturer.ManufacturerFEGetNameMessage;
import tools.message.manufacturer.ManufacturerFEGetProductInfoMessage;
import tools.message.manufacturer.ManufacturerFEGetProductListMessage;
import tools.message.manufacturer.ManufacturerFEProcessPurchaseOrderMessage;
import tools.message.manufacturer.ManufacturerFEReceivePaymentMessage;
import tools.message.manufacturer.ManufacturerSequencerGetNameMessage;
import tools.message.manufacturer.ManufacturerSequencerGetProductInfoMessage;
import tools.message.manufacturer.ManufacturerSequencerGetProductListMessage;
import tools.message.manufacturer.ManufacturerSequencerProcessPurchaseOrderMessage;
import tools.message.manufacturer.ManufacturerSequencerReceivePaymentMessage;

public class ManufacturerSequencerMessageProcesser implements MessageProcesser {

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
				case processPurchaseOrder:
				case getProductInfo:
				case receivePayment:
				case getProductList:
				case getName:
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
		case processPurchaseOrder:
			channelManager.sequencerID++;
			for(Channel channel: channelManager.channelMap.values()){
				if(channel.group == Group.ManufacturerReplica){
					channel.cachedMsg = new ManufacturerSequencerProcessPurchaseOrderMessage(channel.localProcessName
							, ++channel.localSeq
							, channel.peerSeq
							, ((ManufacturerFEProcessPurchaseOrderMessage)msg).item
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
		case getProductInfo:
			channelManager.sequencerID++;
			for(Channel channel: channelManager.channelMap.values()){
				if(channel.group == Group.ManufacturerReplica){
					channel.cachedMsg = new ManufacturerSequencerGetProductInfoMessage(channel.localProcessName
							, ++channel.localSeq
							, channel.peerSeq 
							, ((ManufacturerFEGetProductInfoMessage)msg).aProdName
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
		case receivePayment:
			channelManager.sequencerID++;
			for(Channel channel: channelManager.channelMap.values()){
				if(channel.group == Group.ManufacturerReplica){
					channel.cachedMsg = new ManufacturerSequencerReceivePaymentMessage(channel.localProcessName
							, ++channel.localSeq
							, channel.peerSeq
							, ((ManufacturerFEReceivePaymentMessage)msg).orderNum
							, ((ManufacturerFEReceivePaymentMessage)msg).totalPrice
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
			
		case getProductList:
			channelManager.sequencerID++;
			for(Channel channel: channelManager.channelMap.values()){
				if(channel.group == Group.ManufacturerReplica){
					channel.cachedMsg = new ManufacturerSequencerGetProductListMessage(channel.localProcessName
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
			
		case getName:
			channelManager.sequencerID++;
			for(Channel channel: channelManager.channelMap.values()){
				if(channel.group == Group.ManufacturerReplica){
					channel.cachedMsg = new ManufacturerSequencerGetNameMessage(channel.localProcessName
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
			
		default:
			System.out.println("Unrecognizable action");
			break;
		}
	}
}
