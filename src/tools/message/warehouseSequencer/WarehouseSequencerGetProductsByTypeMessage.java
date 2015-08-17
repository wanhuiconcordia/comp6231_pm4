package tools.message.warehouseSequencer;

import tools.message.warehouseFE.WarehouseFEGetProductsByTypeMessage;

public class WarehouseSequencerGetProductsByTypeMessage extends WarehouseFEGetProductsByTypeMessage{
	
	public int sequencerID;
	public WarehouseSequencerGetProductsByTypeMessage(String sender,
			int senderSeq, int receiverSeq, String productType, int sequencerID) {
		super(sender, senderSeq, receiverSeq, productType);
		
		this.sequencerID = sequencerID;
	}
	
	public String toString(){
		return super.toString() 
				+ ", " + sequencerID;
	}
}
