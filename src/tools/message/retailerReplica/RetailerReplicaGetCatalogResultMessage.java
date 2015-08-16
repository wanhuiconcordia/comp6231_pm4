package tools.message.retailerReplica;

import tools.ItemList;
import tools.message.Action;
import tools.message.Message;
import tools.message.ResultComparator;

public class RetailerReplicaGetCatalogResultMessage extends Message implements
		ResultComparator<RetailerReplicaGetCatalogResultMessage> {
	public ItemList itemList;
	public RetailerReplicaGetCatalogResultMessage(String sender, int senderSeq,
			int receiverSeq, ItemList itemList) {
		super(sender, senderSeq, receiverSeq, Action.getCatelog);
		this.itemList = itemList;
	}

	@Override
	public String toString(){
		return super.toString()
				+ ", " + itemList.toString();
	}
	
	@Override
	public boolean hasSameResult(RetailerReplicaGetCatalogResultMessage other) {
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
