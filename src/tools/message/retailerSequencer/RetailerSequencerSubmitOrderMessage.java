package tools.message.retailerSequencer;

import tools.ItemList;
import tools.message.retailerFE.RetailerFESubmitOrderMessage;

public class RetailerSequencerSubmitOrderMessage extends
		RetailerFESubmitOrderMessage {

	int sequencerID;
	ItemList itemList;
	public RetailerSequencerSubmitOrderMessage(String sender
			, int senderSeq
			, int receiverSeq
			, int customerReferenceNumber
			, ItemList itemList
			, int sequencerID) {
		super(sender, senderSeq, receiverSeq, customerReferenceNumber, itemList);
		this.sequencerID = sequencerID;
		this.itemList = itemList;
	}

}
