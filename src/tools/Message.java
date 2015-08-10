package tools;
import java.io.Serializable;
public abstract class Message implements Serializable{
	public String sender;
	public int senderSeq;
	public int receiverSeq;
	public Action action;
	
	public Message(String sender, int senderSeq, int receiverSeq, Action action){
		this.sender = sender;
		this.senderSeq = senderSeq;
		this.receiverSeq = receiverSeq;
		this.action = action;
	}
	
	public String toString(){
		return sender + ", " + senderSeq + ", " + receiverSeq + ", " + action;  
	}
}
