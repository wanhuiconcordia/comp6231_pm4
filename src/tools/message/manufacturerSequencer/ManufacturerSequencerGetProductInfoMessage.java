package tools.message.manufacturerSequencer;

import tools.message.manufacturerFE.ManufacturerFEGetProductInfoMessage;

public class ManufacturerSequencerGetProductInfoMessage extends ManufacturerFEGetProductInfoMessage{
	public int sequencerID;
	
	public ManufacturerSequencerGetProductInfoMessage(String sender, int senderSeq, int receiverSeq, String aProdName, int sequencerID){
		super(sender,senderSeq,receiverSeq,aProdName);
		this.sequencerID = sequencerID;
		
	}

	public String toString(){
		return super.toString() 
				+ ", " + sequencerID;
	}
}
