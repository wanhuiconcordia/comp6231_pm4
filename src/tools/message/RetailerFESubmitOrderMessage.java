package tools.message;

import tools.ItemList;

public class RetailerFESubmitOrderMessage extends Message {

	ItemList itemList;
	public RetailerFESubmitOrderMessage(String sender
			, int senderSeq
			, int receiverSeq
			, int customerReferenceNumber
			, ItemList itemList) {
		super(sender, senderSeq, receiverSeq, Action.submitOrder);
		this.itemList = itemList;
	}

	public String toString(){
		return super.toString()
				+ ", " + itemList.toString();
	}
}
