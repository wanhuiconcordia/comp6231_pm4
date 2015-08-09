import java.net.SocketException;

import tools.NetworkIO;
import tools.RetailerFEGetCatelogMessage;
import tools.Channel;
public class UDPSocketServer {
	
	public static void main(String[] args) {
		try {
			Channel channel = new Channel("server", "192.168.12.109", 6789);
			NetworkIO networkIO = new NetworkIO(9876);
			networkIO.receiveMessage();
			
			networkIO.sendMsg(new RetailerFEGetCatelogMessage(channel.peerName, channel.localSeq, channel.peerSeq), channel.peerHost, channel.peerPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}

