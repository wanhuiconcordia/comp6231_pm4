package tools.message.manufacturerSequencer;

import tools.Item;
import tools.ItemList;
import tools.message.manufacturerFE.ManufacturerFEProcessPurchaseOrderMessage;

public class ManufacturerSequencerProcessPurchaseOrderMessage extends ManufacturerFEProcessPurchaseOrderMessage{
	public int sequencerID;
	public ItemList itemList;
	public ManufacturerSequencerProcessPurchaseOrderMessage(String sender, int senderSeq, int receiverSeq, ItemList itemList, int sequencerID){
		super(sender, senderSeq, receiverSeq,itemList);
		this.itemList = itemList;
		this.sequencerID = sequencerID;
	}
	
	public String toString(){
		return super.toString() 
				+ ", " + sequencerID;
	}

}
