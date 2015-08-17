package tools.message.rm;

import tools.message.Action;
import tools.message.Message;

public class HeartBeatMessage extends Message {
	public HeartBeatMessage(String sender, int senderSeq, int receiverSeq) {
		super(sender, senderSeq, receiverSeq, Action.HEART_BEAT);
	}
}
