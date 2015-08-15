package tools.message;

public class Packet {
	public String receiverHost;
	public int receiverPort;
	public Message msg;
	public Packet(String receiverHost, int receiverPort, Message msg){
		this.receiverHost = receiverHost;
		this.receiverPort = receiverPort;
		this.msg = msg;
	}
	
	public String toString(){
		return receiverHost + ":" + receiverPort + ", " + msg.toString();
	}
}
