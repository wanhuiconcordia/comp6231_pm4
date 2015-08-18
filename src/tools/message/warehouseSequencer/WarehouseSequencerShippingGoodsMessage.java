package tools.message.warehouseSequencer;

import tools.ItemList;
import tools.message.warehouseFE.WarehouseFEShippingGoodsMessage;

public class WarehouseSequencerShippingGoodsMessage extends WarehouseFEShippingGoodsMessage{
	
	public int sequencerID;
	public ItemList itemList;
	
	public WarehouseSequencerShippingGoodsMessage(String sender, int senderSeq, int receiverSeq,ItemList itemList, int sequencerID){
		super(sender, senderSeq, receiverSeq, itemList);
		this.itemList = itemList;
		this.sequencerID = sequencerID;		
	}

	public String toString(){
		return super.toString() 
				+ ", " + sequencerID;
	}
}
