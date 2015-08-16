package tools.message.warehouse;

import tools.ItemList;
import tools.message.Action;
import tools.message.Message;

public class WarehouseFEShippingGoodsMessage extends Message{
	
	public ItemList itemList;
	public String retailerName;
	
	
	public WarehouseFEShippingGoodsMessage(String sender, int senderSeq, int receiverSeq,ItemList itemList,String retailerName){
		super(sender, senderSeq, receiverSeq, Action.shippingGoods);
		this.itemList  = itemList;
		this.retailerName = retailerName;
		
		
	}

}
