package tools.message;

public class ManufacturerFEGetNameMessage extends Message{
	
	public ManufacturerFEGetNameMessage(String sender, int senderSeq, int receiverSeq){
		super(sender, senderSeq, receiverSeq, Action.getName);
	}

}
