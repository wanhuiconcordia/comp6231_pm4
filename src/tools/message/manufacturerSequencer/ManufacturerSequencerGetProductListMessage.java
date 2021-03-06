package tools.message.manufacturerSequencer;

import tools.message.manufacturerFE.ManufacturerFEGetProductListMessage;

public class ManufacturerSequencerGetProductListMessage extends ManufacturerFEGetProductListMessage{
	public int sequencerID;
	
	public ManufacturerSequencerGetProductListMessage(String sender, int senderSeq, int receiverSeq, int sequencerID){
		super(sender, senderSeq, receiverSeq);
		this.sequencerID = sequencerID;
	}

	public String toString(){
		return super.toString() 
				+ ", " + sequencerID;
	}
}
