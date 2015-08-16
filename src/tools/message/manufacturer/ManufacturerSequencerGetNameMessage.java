package tools.message.manufacturer;

public class ManufacturerSequencerGetNameMessage extends ManufacturerFEGetNameMessage{
	public int sequencerID;
	
	public ManufacturerSequencerGetNameMessage(String sender, int senderSeq, int receiverSeq, int sequencerID){
		
		super(sender,senderSeq,receiverSeq);
		this.sequencerID = sequencerID;
	}

	public String toString(){
		return super.toString() 
				+ ", " + sequencerID;
	}
}
