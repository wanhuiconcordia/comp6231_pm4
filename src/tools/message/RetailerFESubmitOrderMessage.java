package tools.message;

import tools.ItemList;

public class RetailerFESubmitOrderMessage extends Message {

	public ItemList itemList;
	public int customerReferenceNumber;
	public RetailerFESubmitOrderMessage(String sender
			, int senderSeq
			, int receiverSeq
			, int customerReferenceNumber
			, ItemList itemList) {
		super(sender, senderSeq, receiverSeq, Action.submitOrder);
		this.customerReferenceNumber = customerReferenceNumber;
		this.itemList = itemList;
	}

	public String toString(){
		return super.toString()
				+ ", " + customerReferenceNumber
				+ ", " + itemList.toString();
	}
}
