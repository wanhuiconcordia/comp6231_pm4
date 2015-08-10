package tools.message;


public class AckMessage extends Message {

	public AckMessage(String sender, int senderSeq, int receiverSeq) {
		super(sender, senderSeq, receiverSeq, Action.ACK);
	}

}
