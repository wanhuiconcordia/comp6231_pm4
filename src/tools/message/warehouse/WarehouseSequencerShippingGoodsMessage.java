package tools.message.warehouse;

import tools.ItemList;

public class WarehouseSequencerShippingGoodsMessage extends WarehouseFEShippingGoodsMessage{
	
	public int sequencerID;
	public ItemList itemList;
	
	public WarehouseSequencerShippingGoodsMessage(String sender, int senderSeq, int receiverSeq,ItemList itemList,String retailerName, int sequencerID){
		super(sender, senderSeq, receiverSeq, itemList, retailerName);
		this.itemList = itemList;
		this.sequencerID = sequencerID;
		
		
	}

}
