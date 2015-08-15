package tools.message.retailerReplica;

import tools.ItemShippingStatusList;
import tools.message.Action;
import tools.message.Message;
import tools.message.ResultComparator;

public class RetailerReplicaSubmitOrderMessage extends Message implements
		ResultComparator<RetailerReplicaSubmitOrderMessage> {
	public ItemShippingStatusList itemShippingStatusList;
	public RetailerReplicaSubmitOrderMessage(String sender, int senderSeq,
			int receiverSeq, ItemShippingStatusList itemShippingStatusList) {
		super(sender, senderSeq, receiverSeq, Action.submitOrder);
		this.itemShippingStatusList = itemShippingStatusList;
	}

	@Override
	public String toString(){
		return super.toString()
				+ ", " + itemShippingStatusList.toString();
	}
	
	@Override
	public boolean hasSameResult(RetailerReplicaSubmitOrderMessage other) {
		return itemShippingStatusList.isSame(other.itemShippingStatusList);
	}

}
