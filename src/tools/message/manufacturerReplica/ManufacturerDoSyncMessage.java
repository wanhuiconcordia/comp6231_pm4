package tools.message.manufacturerReplica;

import java.util.HashMap;

import tools.Item;
import tools.message.Action;
import tools.message.Message;

public class ManufacturerDoSyncMessage extends Message {
	public HashMap<String, Item> itemsMap;
	public ManufacturerDoSyncMessage(String sender, int senderSeq,
			int receiverSeq, HashMap<String, Item> itemsMap) {
		super(sender, senderSeq, receiverSeq,  Action.doSync);
		this.itemsMap = itemsMap;
	}
	
	public String toString(){
		String retVal = new String();
		for(Item item: itemsMap.values()){
			retVal = retVal + ", " + item.toString();
		}
		return super.toString() + ", " + retVal;
			
	}

}
