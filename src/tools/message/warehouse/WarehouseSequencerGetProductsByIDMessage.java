package tools.message.warehouse;

public class WarehouseSequencerGetProductsByIDMessage extends WarehouseFEGetProductsByIDMessage{

	public int sequencerID;
	public WarehouseSequencerGetProductsByIDMessage(String sender,
			int senderSeq, int receiverSeq, String productID,int sequencerID) {
		super(sender, senderSeq, receiverSeq, productID);
		
		this.sequencerID = sequencerID;
	}
	

}
