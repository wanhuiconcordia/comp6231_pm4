package tools.message.manufacturerSequencer;

import tools.message.manufacturerFE.ManufacturerFEReceivePaymentMessage;

public class ManufacturerSequencerReceivePaymentMessage extends ManufacturerFEReceivePaymentMessage{
	
	public int sequencerID;
	
	public ManufacturerSequencerReceivePaymentMessage(String sender, int senderSeq, int receiverSeq, String orderNum, float totalPrice, int sequencerID){
		super(sender, senderSeq, receiverSeq, orderNum,totalPrice);
		this.sequencerID = sequencerID;
	}
	
	public String toString(){
		return super.toString() 
				+ ", " + sequencerID;
	}
}
