package tools.message.replica;

import tools.message.Action;
import tools.message.Message;

public class AskInitSyncMessage extends Message {

	public AskInitSyncMessage(String sender, int senderSeq, int receiverSeq) {
		super(sender, senderSeq, receiverSeq, Action.askInitSync);
	}

}
