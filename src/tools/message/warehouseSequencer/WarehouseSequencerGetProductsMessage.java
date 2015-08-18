package tools.message.warehouseSequencer;

import tools.message.warehouseFE.WarehouseFEGetProductsMessage;

public class WarehouseSequencerGetProductsMessage extends WarehouseFEGetProductsMessage{
	public int sequencerID;
	public WarehouseSequencerGetProductsMessage(String sender, int senderSeq, int receiverSeq,
			String productID, int sequencerID){
		
		super(sender, senderSeq, receiverSeq, productID);
		this.sequencerID = sequencerID;
	}
	
	public String toString(){
		return super.toString() 
				+ ", " + sequencerID;
	}

}
