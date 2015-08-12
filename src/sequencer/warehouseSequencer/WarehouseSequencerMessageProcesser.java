package sequencer.warehouseSequencer;

import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.message.AckMessage;
import tools.message.Message;
import tools.message.MessageProcesser;
import tools.message.Packet;
import tools.message.WarehouseFEGetProductsMessage;
import tools.message.WarehouseFEGetProductsByIDMessage;
import tools.message.WarehouseFEGetProductsByRegisteredManufacturersMessage;
import tools.message.WarehouseFEGetProductsByTypeMessage;
import tools.message.WarehouseFEShippingGoodsMessage;
import tools.message.WarehouseSequencerGetProductsMessage;
import tools.message.WarehouseSequencerGetProductsByIDMessage;
import tools.message.WarehouseSequencerGetProductsByRegisteredManufacturersMessage;
import tools.message.WarehouseSequencerGetProductsByTypeMessage;
import tools.message.WarehouseSequencerShippingGoodsMessage;

public class WarehouseSequencerMessageProcesser implements MessageProcesser {

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
				case shippingGoods:
				case getProducts:
				case getProductsByRegisteredManufacturers:
				case getProductsByID:
				case getProductsByType:
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
		
		case shippingGoods:
			channelManager.sequencerID++;
			for(Channel channel: channelManager.channelMap.values()){
				if(channel.group == Group.WarehouseReplica){
					channel.cachedMsg = new WarehouseSequencerShippingGoodsMessage(channel.localProcessName
							, ++channel.localSeq
							, channel.peerSeq
							,((WarehouseFEShippingGoodsMessage)msg).itemList 
							, ((WarehouseFEShippingGoodsMessage)msg).retailerName
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
			
		case getProducts:
			channelManager.sequencerID++;
			for(Channel channel: channelManager.channelMap.values()){
				if(channel.group == Group.WarehouseReplica){
					channel.cachedMsg = new WarehouseSequencerGetProductsMessage(channel.localProcessName
							, ++channel.localSeq
							, channel.peerSeq 
							, ((WarehouseFEGetProductsMessage)msg).productID 
							, ((WarehouseFEGetProductsMessage)msg).manufacturerName, channelManager.sequencerID);
					channel.hasCachedMsg = true;
					synchronized(channelManager.outgoingPacketQueueLock) {
						channelManager.outgoingPacketQueue.add(new Packet(channel.peerHost
								, channel.peerPort
								, channel.cachedMsg));
					}
				}
			}			
			break;
			
		case getProductsByRegisteredManufacturers:
			channelManager.sequencerID++;
			for(Channel channel: channelManager.channelMap.values()){
				if(channel.group == Group.WarehouseReplica){
					channel.cachedMsg = new WarehouseSequencerGetProductsByRegisteredManufacturersMessage(channel.localProcessName
							, ++channel.localSeq
							, channel.peerSeq
							, ((WarehouseFEGetProductsByRegisteredManufacturersMessage)msg).manufacturerName
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
			
		case getProductsByID:
			channelManager.sequencerID++;
			for(Channel channel: channelManager.channelMap.values()){
				if(channel.group == Group.WarehouseReplica){
					channel.cachedMsg = new WarehouseSequencerGetProductsByIDMessage(channel.localProcessName
							, ++channel.localSeq
							, channel.peerSeq
							, ((WarehouseFEGetProductsByIDMessage)msg).productID
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
			
		case getProductsByType:
			channelManager.sequencerID++;
			for(Channel channel: channelManager.channelMap.values()){
				if(channel.group == Group.WarehouseReplica){
					channel.cachedMsg = new WarehouseSequencerGetProductsByTypeMessage(channel.localProcessName
							, ++channel.localSeq
							, channel.peerSeq
							, ((WarehouseFEGetProductsByTypeMessage)msg).productType
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
