package tools.message.warehouseReplica;

import java.util.HashMap;

import tools.Item;
import tools.message.Action;
import tools.message.Message;

public class WarehouseDoSyncMessage extends Message {
	public HashMap<String, Item> inventoryItemMap;
	public WarehouseDoSyncMessage(String sender, int senderSeq,
			int receiverSeq, HashMap<String, Item> inventoryItemMap) {
		super(sender, senderSeq, receiverSeq, Action.doSync);
		this.inventoryItemMap = inventoryItemMap;
	}
	
	public String toString(){
		String retVal = new String();
		for(Item item: inventoryItemMap.values()){
			retVal = retVal + ", " + item.toString();
		}
		return super.toString() + ", " + retVal;
	}

}
