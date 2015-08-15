package manufacturerSequencer;

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
import tools.message.WarehouseFEGetProductsByIDMessage;
import tools.message.WarehouseFEGetProductsByRegisteredManufacturersMessage;
import tools.message.WarehouseFEGetProductsByTypeMessage;
import tools.message.WarehouseFEGetProductsMessage;
import tools.message.WarehouseFEShippingGoodsMessage;
import tools.message.WarehouseSequencerGetProductsByIDMessage;
import tools.message.WarehouseSequencerGetProductsByRegisteredManufacturersMessage;
import tools.message.WarehouseSequencerGetProductsMessage;
import tools.message.WarehouseSequencerShippingGoodsMessage;

public class ManufacturerSequencerMessageProcesser implements MessageProcesser {

	@Override
	public void processMessage(ChannelManager channelManager, Message msg) {
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
				case shippingGoods:
				case getProducts:
				case getProductsByRegisteredManufacturers:
				case getProductsByID:
				case getProductsByType:
					channelManager.sequencerID++;
					for(Channel castChannel: channelManager.channelMap.values()){
						if(castChannel.group == Group.RetailerReplica){
							castChannel.cachedMsg = generateWarehouseSequencerMessage(castChannel.localProcessName
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
	
	private Message generateWarehouseSequencerMessage(String localProcessName
			, int localSeq
			, int peerSeq
			, int sequencerID
			, Message receivedMsg){
		switch(receivedMsg.action){
		case shippingGoods:
					return new WarehouseSequencerShippingGoodsMessage(localProcessName
							, localSeq
							, peerSeq
							,((WarehouseFEShippingGoodsMessage)receivedMsg).itemList 
							, ((WarehouseFEShippingGoodsMessage)receivedMsg).retailerName
							, sequencerID); 
		case getProducts:
			return new WarehouseSequencerGetProductsMessage(localProcessName
							, localSeq
							, peerSeq 
							, ((WarehouseFEGetProductsMessage)receivedMsg).productID 
							, ((WarehouseFEGetProductsMessage)receivedMsg).manufacturerName
							, sequencerID);
		case getProductsByRegisteredManufacturers:
			return new WarehouseSequencerGetProductsByRegisteredManufacturersMessage(localProcessName
					, localSeq
					, peerSeq
					, ((WarehouseFEGetProductsByRegisteredManufacturersMessage)receivedMsg).manufacturerName
					, sequencerID);
	
		case getProductsByID:
			return new WarehouseSequencerGetProductsByIDMessage(localProcessName
							, localSeq
							, peerSeq
							, ((WarehouseFEGetProductsByIDMessage)receivedMsg).productID
							, sequencerID);
		case getProductsByType:
			return new WarehouseSequencerGetProductsByIDMessage(localProcessName
							, localSeq
							, peerSeq
							, ((WarehouseFEGetProductsByTypeMessage)receivedMsg).productType
							, sequencerID);
		}
		return null;
	}
}
