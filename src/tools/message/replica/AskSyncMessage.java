package tools.message.replica;

import tools.message.Action;
import tools.message.Message;

public class AskSyncMessage extends Message {

	public AskSyncMessage(String sender, int senderSeq, int receiverSeq) {
		super(sender, senderSeq, receiverSeq, Action.askSync);
	}

}
