package tools.message;

import tools.ItemList;

public class WarehouseFEShippingGoodsMessage extends Message{
	public ItemList itemList;
	public String reatailerName;
	
	
	public WarehouseFEShippingGoodsMessage(String sender, int senderSeq, int receiverSeq,ItemList itemlist,String retailerName){
		super(sender, senderSeq, receiverSeq, Action.shippingGoods);
		this.itemList  = itemList;
		this.reatailerName = retailerName;
		
		
	}

}
