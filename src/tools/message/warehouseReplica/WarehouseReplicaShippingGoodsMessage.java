package tools.message.warehouseReplica;

import tools.ItemList;
import tools.message.Action;
import tools.message.Message;
import tools.message.ResultComparator;

public class WarehouseReplicaShippingGoodsMessage extends Message implements ResultComparator<WarehouseReplicaShippingGoodsMessage>{
	public ItemList itemList;

	public WarehouseReplicaShippingGoodsMessage(String sender, int senderSeq
			, int receiverSeq
			, ItemList itemList) {
		super(sender, senderSeq, receiverSeq, Action.getProductsByID);
		this.itemList = itemList;
	}
	
	public String toString(){
		return super.toString() + ", " + itemList.toString();
	}

	@Override
	public boolean hasSameResult(WarehouseReplicaShippingGoodsMessage other) {
		if(itemList == null){
			if(other.itemList == null){
				return true;
			}else{
				return false;
			}
		}else{
			if(other.itemList == null){
				return false;
			}else{
				return itemList.isSame(other.itemList);
			}
		}
	} 
}
