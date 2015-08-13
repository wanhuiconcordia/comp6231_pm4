package tools.message;

import tools.Item;

public class ManufacturerFEProcessPurchaseOrderMessage extends Message{
	
	public Item item;
	
	public ManufacturerFEProcessPurchaseOrderMessage(String sender, int senderSeq, int receiverSeq, Item item){
		super(sender, senderSeq, receiverSeq, Action.processPurchaseOrder);
		this.item = item;
	}
}
