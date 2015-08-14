package tools.message.warehouse;

public class WarehouseSequencerGetProductsByTypeMessage extends WarehouseFEGetProductsByTypeMessage{
	
	public int sequencerID;
	public WarehouseSequencerGetProductsByTypeMessage(String sender,
			int senderSeq, int receiverSeq, String productType, int sequencerID) {
		super(sender, senderSeq, receiverSeq, productType);
		
		this.sequencerID = sequencerID;
	}
	

}
