package warehouse;

import java.util.HashMap;

import manufacturer.ManufacturerInterface;
import tools.Item;
import tools.ItemList;
import tools.Product;
import tools.ProductList;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.message.Action;
import tools.message.Message;
import tools.message.MessageProcesser;
import tools.message.Packet;
import tools.message.warehouseFE.WarehouseFEGetProductsByIDMessage;
import tools.message.warehouseReplica.WarehouseDoSyncMessage;
import tools.message.warehouseReplica.WarehouseReplicaGetProductsByIDMessage;
import tools.message.warehouseReplica.WarehouseReplicaShippingGoodsMessage;
import tools.message.warehouseSequencer.WarehouseSequencerGetProductsByIDMessage;
import tools.message.warehouseSequencer.WarehouseSequencerShippingGoodsMessage;
import tools.message.replica.AskSyncMessage;
import tools.message.rm.RMSyncMessage;

public class WarehouseReplicaMessageProcesser extends MessageProcesser{
	public WarehouseReplica warehouseReplica;

	public WarehouseReplicaMessageProcesser(WarehouseReplica warehouseReplica){
		this.warehouseReplica = warehouseReplica;
	}

	@Override
	public void processNewRequest(ChannelManager channelManager,
			Channel channel, Message msg) {
		if(msg.action == Action.ACK){
			channel.isWaitingForRespose = false;
		}else{
			channel.receivedMessage = msg;
			switch(msg.action){
			case INIT:
				channel.localSeq = 0;
				ackBack(channelManager, channel);
				break;
			case askInitSync:
				channel.localSeq = 0;
			case askSync:
				channel.peerSeq = msg.senderSeq;
				channel.backupPacket = new Packet(channel.peerProcessName, channel.peerHost
						, channel.peerPort
						, new WarehouseDoSyncMessage(channel.localProcessName
								, ++channel.localSeq
								, channel.peerSeq
								, warehouseReplica.inventoryManager.inventoryItemMap));
				synchronized (channelManager.outgoingPacketQueueLock) {
					channelManager.outgoingPacketQueue.add(channel.backupPacket);
				}
				break;
			case doSync:
				ackBack(channelManager, channel);
				WarehouseDoSyncMessage doSyncMessage = (WarehouseDoSyncMessage)msg;
				warehouseReplica.inventoryManager.inventoryItemMap = doSyncMessage.inventoryItemMap;
				channel.isWaitingForRespose = false;
				break;
			case sync:
				ackBack(channelManager, channel);
				RMSyncMessage syncMsg = (RMSyncMessage)msg;

				if(channelManager.channelMap.containsKey(syncMsg.goodReplicaName)){
					Channel replicaChannel = channelManager.channelMap.get(syncMsg.goodReplicaName);
					replicaChannel.backupPacket = new Packet(replicaChannel.peerProcessName, replicaChannel.peerHost
							, replicaChannel.peerPort
							, new AskSyncMessage(replicaChannel.localProcessName
									, ++replicaChannel.localSeq
									, replicaChannel.peerSeq));
				}else{
					System.out.println("Wrong. Cannot find " + syncMsg.goodReplicaName);
				}
				break;
			case HEART_BEAT:
				ackBack(channelManager, channel);
				break;

			case shippingGoods:
				ackBack(channelManager, channel);

				if(channelManager.channelMap.containsKey(warehouseReplica.baseName 
						+ warehouseReplica.warehouseIndex + "FE")){
					Channel FEChannel = channelManager.channelMap.get(warehouseReplica.baseName 
							+ warehouseReplica.warehouseIndex + "FE");

					ItemList retItemList = new ItemList();
					WarehouseSequencerShippingGoodsMessage shippingGoodsMsg
					= (WarehouseSequencerShippingGoodsMessage)msg;
					
					ItemList itemList = shippingGoodsMsg.itemList;
					HashMap<String, Item>  tmpMap = warehouseReplica.inventoryManager.inventoryItemMap;
					for(Item item: itemList.innerItemList){
						if(tmpMap.containsKey(item.productID)){
							Item item2 = tmpMap.get(item.productID);
							
							if(item2.quantity > item.quantity){
								retItemList.addItem(item);
								item2.quantity -= item.quantity;
							}else{
								retItemList.addItem(item2.clone());
								item2.quantity = 0;
							}
						}
					}
					
					warehouseReplica.inventoryManager.saveItems();
					
					replenish(shippingGoodsMsg.sequencerID);
					
					
					
					Message responsMsg = new WarehouseReplicaShippingGoodsMessage(FEChannel.localProcessName
							, ++FEChannel.localSeq
							, FEChannel.peerSeq
							, retItemList);
					FEChannel.backupPacket = new Packet(FEChannel.peerProcessName, FEChannel.peerHost,  FEChannel.peerPort, responsMsg); 
					FEChannel.isWaitingForRespose = true;
				}	
				
				
				break;
			case getProducts:
				ackBack(channelManager, channel);
				break;
			case getProductsByID:
				ackBack(channelManager, channel);

				if(channelManager.channelMap.containsKey(warehouseReplica.baseName 
						+ warehouseReplica.warehouseIndex + "FE")){
					Channel FEChannel = channelManager.channelMap.get(warehouseReplica.baseName 
							+ warehouseReplica.warehouseIndex + "FE");

					WarehouseSequencerGetProductsByIDMessage getProductByidMsg
					= (WarehouseSequencerGetProductsByIDMessage)msg;
					if(warehouseReplica.inventoryManager.inventoryItemMap.values().size() == 0){
						for(ManufacturerInterface manufacturerFE: warehouseReplica.manufacturerFEList){
							ProductList productList = manufacturerFE.getProductList(getProductByidMsg.sequencerID);
							for(Product product: productList.innerProductList){
								warehouseReplica.inventoryManager.inventoryItemMap.put(product.productID , new Item(product, 0));
							}
						}
						warehouseReplica.inventoryManager.saveItems();
					}
					ItemList itemList = new ItemList();
					for(Item item: warehouseReplica.inventoryManager.inventoryItemMap.values()){
						//if(getProductByidMsg.productID.equals(item.productID))
						itemList.addItem(item);

					}
					
					Message responsMsg = new WarehouseReplicaGetProductsByIDMessage(FEChannel.localProcessName
							, ++FEChannel.localSeq
							, FEChannel.peerSeq
							, itemList);
					FEChannel.backupPacket = new Packet(FEChannel.peerProcessName, FEChannel.peerHost,  FEChannel.peerPort, responsMsg); 
					FEChannel.isWaitingForRespose = true;
				}				
				break;
			case getProductsByType:
				ackBack(channelManager, channel);
				break;

			default:
				break;
			}
		}
	}

	@Override
	public void processDuplicaRequest(ChannelManager channelManager,
			Channel channel, Message msg) {
		// TODO Auto-generated method stub

	}

	public void replenish(int sequencerID){
		HashMap<String, ItemList> itemListMap = new HashMap<String, ItemList>();
		
		for(Item item: warehouseReplica.inventoryManager.inventoryItemMap.values()){
			if(item.quantity < 400){
				Item tmpItem = new Item(item);
				tmpItem.quantity = 400;
				if(itemListMap.containsKey(tmpItem.manufacturerName)){
					itemListMap.get(tmpItem.manufacturerName).addItem(tmpItem);
				}else{
					ItemList itemList = new ItemList();
					itemList.addItem(tmpItem);
					itemListMap.put(tmpItem.manufacturerName, itemList);
				}
				
			}
		}
		
		for(ItemList itemList: itemListMap.values()){
			String manufacturerName = itemList.innerItemList.get(0).manufacturerName;
			
			int indexx = manufacturerName.charAt(manufacturerName.length() - 1) - 48;
			if(warehouseReplica.manufacturerFEList.get(indexx).processPurchaseOrder(itemList, sequencerID)){
				for(Item tmpItem: itemList.innerItemList){
					tmpItem.quantity += 400;
				}
			}
		}
		warehouseReplica.inventoryManager.saveItems();
	}
}
