package tools.message.warehouseFE;

import tools.ItemList;
import tools.message.Action;
import tools.message.Message;

public class WarehouseFEShippingGoodsMessage extends Message{
	
	public ItemList itemList;
	
	public WarehouseFEShippingGoodsMessage(String sender, int senderSeq, int receiverSeq,ItemList itemList){
		super(sender, senderSeq, receiverSeq, Action.shippingGoods);
		this.itemList  = itemList;
		
		
	}
	
	public String toString(){
		return super.toString() 
				+ ", " + itemList.toString();
	}

}
