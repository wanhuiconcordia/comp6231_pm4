package tools;
import java.io.Serializable;
public abstract class Message implements Serializable{
	public String sender;
	public int senderSeq;
	public int receiverSeq;
	public Action action;
	boolean needAck;
	
	public Message(String sender, int senderSeq, int receiverSeq, Action action, boolean needAck){
		this.sender = sender;
		this.senderSeq = senderSeq;
		this.receiverSeq = receiverSeq;
		this.action = action;
		this.needAck = needAck;
	}
}
