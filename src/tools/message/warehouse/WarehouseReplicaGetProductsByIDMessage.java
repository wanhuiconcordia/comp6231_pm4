package tools.message.warehouse;

import tools.ItemList;
import tools.message.Action;
import tools.message.Message;
import tools.message.ResultComparator;

public class WarehouseReplicaGetProductsByIDMessage extends Message implements ResultComparator<WarehouseReplicaGetProductsByIDMessage>{
	public ItemList itemList;

	public WarehouseReplicaGetProductsByIDMessage(String sender, int senderSeq
			, int receiverSeq
			, ItemList itemList) {
		super(sender, senderSeq, receiverSeq, Action.getProductsByID);
		this.itemList = itemList;
	}

	@Override
	public boolean hasSameResult(WarehouseReplicaGetProductsByIDMessage other) {
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
