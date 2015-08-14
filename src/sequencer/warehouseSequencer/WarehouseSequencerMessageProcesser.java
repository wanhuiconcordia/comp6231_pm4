package sequencer.warehouseSequencer;

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
