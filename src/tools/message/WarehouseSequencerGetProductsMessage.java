package tools.message;

public class WarehouseSequencerGetProductsMessage extends WarehouseFEGetProductsMessage{
	public int sequencerID;
	public WarehouseSequencerGetProductsMessage(String sender, int senderSeq, int receiverSeq,
			String productID, String manufacturerName, int sequencerID){
		
		super(sender, senderSeq, receiverSeq, productID, manufacturerName);
		this.sequencerID = sequencerID;
	}

}
