package tools.message;

public class Packet {
	public String receiver;
	public String receiverHost;
	public int receiverPort;
	public Message msg;
	public Packet(String receiver, String receiverHost, int receiverPort, Message msg){
		this.receiver = receiver;
		this.receiverHost = receiverHost;
		this.receiverPort = receiverPort;
		this.msg = msg;
	}
	
	public String toString(){
		return "receiver:" + receiver 
				+ "(" + receiverHost + ":" + receiverPort + "), " + msg.toString();
	}
}
