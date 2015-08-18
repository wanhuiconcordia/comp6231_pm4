package tools.message.manufacturerFE;

import tools.Item;
import tools.ItemList;
import tools.message.Action;
import tools.message.Message;

public class ManufacturerFEProcessPurchaseOrderMessage extends Message{
	
	public ItemList itemList;
	
	public ManufacturerFEProcessPurchaseOrderMessage(String sender, int senderSeq, int receiverSeq, ItemList itemList){
		super(sender, senderSeq, receiverSeq, Action.processPurchaseOrder);
		this.itemList = itemList;
	}
	
	public String toString(){
		return super.toString() 
				+ ", " + itemList.toString();
	}
}
