package tools.message;

import tools.ItemList;

public class RetailerSequencerSubmitOrderMessage extends
		RetailerFESubmitOrderMessage {

	int sequencerID;
	public RetailerSequencerSubmitOrderMessage(String sender
			, int senderSeq
			, int receiverSeq
			, int customerReferenceNumber
			, ItemList itemList
			, int sequencerID) {
		super(sender, senderSeq, receiverSeq, customerReferenceNumber, itemList);
		this.sequencerID = sequencerID;
	}

}
