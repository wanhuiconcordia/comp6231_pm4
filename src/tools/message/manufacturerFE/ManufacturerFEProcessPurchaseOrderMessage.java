package tools.message.manufacturerFE;

import tools.Item;
import tools.message.Action;
import tools.message.Message;

public class ManufacturerFEProcessPurchaseOrderMessage extends Message{
	
	public Item item;
	
	public ManufacturerFEProcessPurchaseOrderMessage(String sender, int senderSeq, int receiverSeq, Item item){
		super(sender, senderSeq, receiverSeq, Action.processPurchaseOrder);
		this.item = item;
	}
	
	public String toString(){
		return super.toString() 
				+ ", " + item.toString();
	}
}
