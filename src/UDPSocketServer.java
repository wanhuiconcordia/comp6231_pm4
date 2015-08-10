import java.net.SocketException;

import tools.channel.Channel;
import tools.channel.Group;
import tools.channel.NetworkIO;
import tools.message.RetailerFEGetCatelogMessage;
import tools.message.RetailerFESignInMessage;
public class UDPSocketServer {
	
	public static void main(String[] args) {
		try {
			Channel channel = new Channel("UDPSocketClient", "UDPSocketServer", "localhost", 6789, Group.RetailerFE, null);
			NetworkIO networkIO = new NetworkIO(9876);
			networkIO.receiveMessage();
			
//			networkIO.sendMsg(new RetailerFEGetCatelogMessage(channel.peerName, channel.localSeq, channel.peerSeq), channel.peerHost, channel.peerPort);
			networkIO.sendMsg(new RetailerFESignInMessage(channel.peerProcessName, channel.localSeq, channel.peerSeq, 1234567, "password_abc"), channel.peerHost, channel.peerPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}

