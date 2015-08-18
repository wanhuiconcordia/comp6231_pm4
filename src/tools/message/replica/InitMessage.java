package tools.message.replica;

import tools.message.Action;
import tools.message.Message;

public class InitMessage extends Message {

	public InitMessage(String sender, int senderSeq, int receiverSeq) {
		super(sender, senderSeq, receiverSeq, Action.INIT);
	}

}
