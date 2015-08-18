package retailer;

import java.util.ArrayList;
import java.util.HashMap;

import tools.Customer;
import tools.Item;
import tools.ItemList;
import tools.ItemShippingStatus;
import tools.ItemShippingStatusList;
import tools.SignUpResult;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.message.Action;
import tools.message.Message;
import tools.message.MessageProcesser;
import tools.message.Packet;
import tools.message.replica.AskSyncMessage;
import tools.message.retailerFE.RetailerFEGetCatelogMessage;
import tools.message.retailerFE.RetailerFESignInMessage;
import tools.message.retailerFE.RetailerFESignUpMessage;
import tools.message.retailerReplica.RetailerDoSyncMessage;
import tools.message.retailerReplica.RetailerReplicaGetCatalogResultMessage;
import tools.message.retailerReplica.RetailerReplicaSignInResultMessage;
import tools.message.retailerReplica.RetailerReplicaSignUpReultMessage;
import tools.message.retailerReplica.RetailerReplicaSubmitOrderMessage;
import tools.message.retailerSequencer.RetailerSequencerGetCatelogMessage;
import tools.message.retailerSequencer.RetailerSequencerSignInMessage;
import tools.message.retailerSequencer.RetailerSequencerSignUpMessage;
import tools.message.retailerSequencer.RetailerSequencerSubmitOrderMessage;
import tools.message.rm.RMSyncMessage;
import warehouse.WarehouseFE;
import warehouse.WarehouseInterface;

public class RetailerReplicaMessageProcesser extends MessageProcesser {

	public RetailerReplica retailerReplica;

	public RetailerReplicaMessageProcesser(RetailerReplica retailerReplica){
		this.retailerReplica = retailerReplica;
	}

	@Override
	public void processNewRequest(ChannelManager channelManager, Channel channel, Message msg) {
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
						, new RetailerDoSyncMessage(channel.localProcessName
								, ++channel.localSeq
								, channel.peerSeq
								, retailerReplica.customerManager.customers));
				synchronized (channelManager.outgoingPacketQueueLock) {
					channelManager.outgoingPacketQueue.add(channel.backupPacket);
				}
				break;
			case doSync:
				ackBack(channelManager, channel);
				RetailerDoSyncMessage doSyncMessage = (RetailerDoSyncMessage)msg;
				retailerReplica.customerManager.customers = doSyncMessage.customerList;
				retailerReplica.customerManager.saveCustomers();
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

			case signIn:
				ackBack(channelManager, channel);
				RetailerSequencerSignInMessage signInMsg = (RetailerSequencerSignInMessage)msg;
				Customer customer = retailerReplica.customerManager.find(signInMsg.customerReferenceNumber, signInMsg.password);
				if(channelManager.channelMap.containsKey("RetailerFE")){
					Channel FEChannel = channelManager.channelMap.get("RetailerFE");
					Message responsMsg = new RetailerReplicaSignInResultMessage(FEChannel.localProcessName
							, ++FEChannel.localSeq
							, FEChannel.peerSeq
							, customer);
					FEChannel.backupPacket = new Packet(FEChannel.peerProcessName, FEChannel.peerHost,  FEChannel.peerPort, responsMsg); 
					FEChannel.isWaitingForRespose = true;
				}

				break;
			case signUp:
				ackBack(channelManager, channel);
				RetailerSequencerSignUpMessage signUpMsg = (RetailerSequencerSignUpMessage)msg;
				SignUpResult signUpResult= retailerReplica.customerManager.register(signUpMsg.name
						, signUpMsg.password
						, signUpMsg.street1
						, signUpMsg.street2
						, signUpMsg.city
						, signUpMsg.state
						, signUpMsg.zip
						, signUpMsg.country);

				if(channelManager.channelMap.containsKey("RetailerFE")){
					Channel FEChannel = channelManager.channelMap.get("RetailerFE");

					//TRIGGER AN ERROR.
					if(FEChannel.localProcessName.endsWith("4")){
						signUpResult = new SignUpResult(true, 10, "good signup");
					}
					Message responsMsg = new RetailerReplicaSignUpReultMessage(FEChannel.localProcessName
							, ++FEChannel.localSeq
							, FEChannel.peerSeq
							, signUpResult);
					FEChannel.backupPacket = new Packet(FEChannel.peerProcessName, FEChannel.peerHost,  FEChannel.peerPort, responsMsg); 
					FEChannel.isWaitingForRespose = true;
				}

				break;

			case getCatelog:
				ackBack(channelManager, channel);

				RetailerSequencerGetCatelogMessage getCatalogMsg = (RetailerSequencerGetCatelogMessage)msg;
				ItemList itemList = new ItemList();
				HashMap<String, Item> itemsMap = new HashMap<String, Item>();
				
				retailerReplica.loggerClient.write("called get products");

				for(int i = 0; i < retailerReplica.warehouseFEList.size(); i++){
					ItemList itemListFromWarehouse = retailerReplica.warehouseFEList.get(i).getProductsByID("", getCatalogMsg.sequencerID);
					for(Item item: itemListFromWarehouse.innerItemList){
						String key = item.productID;
						Item itemInMap = itemsMap.get(key); 
						if(itemInMap == null){
							itemsMap.put(key, item.clone());
						}else{
							itemInMap.quantity = itemInMap.quantity + item.quantity;
						}
					}
				}

				for(Item item: itemsMap.values()){
					itemList.innerItemList.add(item);
				}

				if(channelManager.channelMap.containsKey("RetailerFE")){
					Channel FEChannel = channelManager.channelMap.get("RetailerFE");
					Message responsMsg = new RetailerReplicaGetCatalogResultMessage(FEChannel.localProcessName
							, ++FEChannel.localSeq
							, FEChannel.peerSeq
							, itemList);
					FEChannel.backupPacket = new Packet(FEChannel.peerProcessName, FEChannel.peerHost,  FEChannel.peerPort, responsMsg); 
					FEChannel.isWaitingForRespose = true;
				}

				break;
			case submitOrder:
				ackBack(channelManager, channel);
				RetailerSequencerSubmitOrderMessage submitOrderMsg = (RetailerSequencerSubmitOrderMessage)msg;

				ItemShippingStatusList itemShippingStatusList= new ItemShippingStatusList();
				Customer currentCustomer = retailerReplica.customerManager.getCustomerByReferenceNumber(submitOrderMsg.customerReferenceNumber);
				if(currentCustomer != null 
						&& submitOrderMsg.itemList == null 
						&& submitOrderMsg.itemList.innerItemList.isEmpty()){
					HashMap<String, ItemShippingStatus> receivedItemShippingStatusMap = new HashMap<String, ItemShippingStatus>();
					HashMap<String, Item> orderMap = new HashMap<String, Item>();
					for(Item item: submitOrderMsg.itemList.innerItemList){
						Item itemImpl = new Item(item);
						System.out.println("item orderd"+itemImpl.toString());
						if(itemImpl.quantity > 0){
							Item itemInOrderMap = orderMap.get(itemImpl.productID);
							if(itemInOrderMap == null){
								orderMap.put(item.productID, new Item(itemImpl));
							}else{
								itemInOrderMap.quantity += itemImpl.quantity;
							}
						}
					}
					System.out.println("order map:" +orderMap);

					for(WarehouseInterface thisWarehouse: retailerReplica.warehouseFEList){
						int itemRequestFromWarehouseCount = orderMap.size();

						if(itemRequestFromWarehouseCount > 0)
						{
							ItemList itemRequestFromWarehouseList = new ItemList(itemRequestFromWarehouseCount);
							System.out.println("itemRequestFromWarehouseList size : "+ itemRequestFromWarehouseList.innerItemList.size());
							int i = 0;
							for(Item orderItem: orderMap.values()){
								System.out.println("orderItem: "+ orderItem);
								itemRequestFromWarehouseList.innerItemList.add(i, orderItem);
								i++;
							}
							System.out.println("itemRequestFromWarehouseList size after adding: "+ itemRequestFromWarehouseList.innerItemList.size());
							ItemList itemsGotFromCurrentWarehouse=null;

							itemsGotFromCurrentWarehouse = thisWarehouse.shippingGoods(itemRequestFromWarehouseList, submitOrderMsg.sequencerID);

							if(itemsGotFromCurrentWarehouse == null){
								System.out.println("warehouse return null");
							}else if(itemsGotFromCurrentWarehouse.innerItemList.isEmpty()){
								System.out.println("warehouse return empty arrry");
							}else{
								for(Item item: itemsGotFromCurrentWarehouse.innerItemList){
									Item itemInReceivedItemShippingStatusMap = receivedItemShippingStatusMap.get(item.productID);
									if(itemInReceivedItemShippingStatusMap == null){
										receivedItemShippingStatusMap.put(item.productID, new ItemShippingStatus(item, true));
									}else{
										itemInReceivedItemShippingStatusMap.quantity += item.quantity;
									}

									Item itemInOrderMap = orderMap.get(item.productID);
									if(itemInOrderMap == null){
										System.out.println("Warehouse side error. never request this item from warehouse, but the warehouse return this item.");
									}else{
										itemInOrderMap.quantity -= item.quantity;
										if(itemInOrderMap.quantity == 0){
											orderMap.remove(item.productID);
										}
									}
								}
							}
						}else{
							break;
						}
					}

					ArrayList<ItemShippingStatus> tmpItemShippingStatusList = new ArrayList<ItemShippingStatus>();

					for(ItemShippingStatus itemInReceivedItemShippingStatusMap: receivedItemShippingStatusMap.values()){
						tmpItemShippingStatusList.add(itemInReceivedItemShippingStatusMap);
					}

					for(Item itemInOrderMap: orderMap.values()){
						tmpItemShippingStatusList.add(new ItemShippingStatus(itemInOrderMap, false));
					}

					itemShippingStatusList.setItems(tmpItemShippingStatusList);
				}

				if(channelManager.channelMap.containsKey("RetailerFE")){
					Channel FEChannel = channelManager.channelMap.get("RetailerFE");
					Message responsMsg = new RetailerReplicaSubmitOrderMessage(FEChannel.localProcessName
							, ++FEChannel.localSeq
							, FEChannel.peerSeq
							, itemShippingStatusList);
					FEChannel.backupPacket = new Packet(FEChannel.peerProcessName, FEChannel.peerHost,  FEChannel.peerPort, responsMsg); 
					FEChannel.isWaitingForRespose = true;
				}

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

}
