package tools.message.manufacturer;

import tools.Item;

public class ManufacturerSequencerProcessPurchaseOrderMessage extends ManufacturerFEProcessPurchaseOrderMessage{
	public int sequencerID;
	public Item item;
	public ManufacturerSequencerProcessPurchaseOrderMessage(String sender, int senderSeq, int receiverSeq, Item item, int sequencerID){
		super(sender, senderSeq, receiverSeq,item);
		this.item = item;
		this.sequencerID = sequencerID;
	}
	
	public String toString(){
		return super.toString() 
				+ ", " + sequencerID;
	}

}
