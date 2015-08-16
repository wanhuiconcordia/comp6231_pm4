package sequencer.manufacturerSequencer;

import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.message.AckMessage;
import tools.message.Action;
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


public class ManufacturerSequencerMessageProcesser extends MessageProcesser {

	@Override
	public void processNewRequest(ChannelManager channelManager, Channel channel, Message msg) {
		if(msg.action == Action.ACK){
			channel.isWaitingForRespose = false;
		}else{
			channel.receivedMessage = msg;
			ackBack(channelManager, channel);

			switch(msg.action){
			case processPurchaseOrder:
			case getProductInfo:
			case receivePayment:
			case getProductList:
			case getName:
				channelManager.sequencerID++;
				for(Channel replicaChannel: channelManager.channelMap.values()){
					if(replicaChannel.group == Group.REPLICA){
						replicaChannel.backupPacket = new Packet(replicaChannel.peerProcessName, replicaChannel.peerHost
								, replicaChannel.peerPort
								, generateManufacturerSequencerMessage(replicaChannel.localProcessName
										, ++replicaChannel.localSeq
										, replicaChannel.peerSeq
										, channelManager.sequencerID
										, msg));
						
						replicaChannel.isWaitingForRespose = true;
						synchronized(channelManager.outgoingPacketQueueLock) {
							channelManager.outgoingPacketQueue.add(replicaChannel.backupPacket);
							System.out.println("put this packet in outgoint queue:" + replicaChannel.backupPacket.toString());
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
	
	private Message generateManufacturerSequencerMessage(String localProcessName
			, int localSeq
			, int peerSeq
			, int sequencerID
			, Message receivedMsg){
		
	System.out.println("generateWarehouseSequencerMessage() is called.");
	switch(receivedMsg.action){
		case processPurchaseOrder:
			return new ManufacturerSequencerProcessPurchaseOrderMessage(localProcessName
					, localSeq
					, peerSeq
					, ((ManufacturerFEProcessPurchaseOrderMessage)receivedMsg).item
					, sequencerID); 
		case getProductInfo:
			return new ManufacturerSequencerProcessPurchaseOrderMessage(localProcessName
					, localSeq
					, peerSeq
					, ((ManufacturerFEProcessPurchaseOrderMessage)receivedMsg).item
					, sequencerID); 
		case receivePayment:
			return new ManufacturerSequencerReceivePaymentMessage(localProcessName
					, localSeq
					, peerSeq
					, ((ManufacturerFEReceivePaymentMessage)receivedMsg).orderNum
					, ((ManufacturerFEReceivePaymentMessage)receivedMsg).totalPrice
					, sequencerID);
	
		case getProductList:
			return  new ManufacturerSequencerGetProductListMessage(localProcessName
					, localSeq
					, peerSeq
					, sequencerID); 
		case getName:
			return new ManufacturerSequencerGetNameMessage(localProcessName
					, localSeq
					, peerSeq
					, sequencerID); 
		}
	
	System.out.println("Bad action");
	return null;
	}

	
}
