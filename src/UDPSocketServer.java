import java.net.SocketException;
import java.net.SocketTimeoutException;

import tools.channel.Channel;
import tools.channel.Group;
import tools.channel.NetworkIO;
import tools.message.retailerFE.RetailerFESignInMessage;
public class UDPSocketServer {

	public static void main(String[] args) {
		Channel channel = new Channel("UDPSocketClient", "UDPSocketServer", "localhost", 6789, Group.FE);
		NetworkIO networkIO;
		try {
			networkIO = new NetworkIO(9876);
			while(true){
				try {
					networkIO.receiveMessage();
					break;
				} catch (SocketTimeoutException e) {
					//time out 
					//e.printStackTrace();
				}
			}
			networkIO.sendMsg(new RetailerFESignInMessage(channel.peerProcessName, channel.localSeq
					, channel.peerSeq, 1234567, "password_abc"), channel.peerHost, channel.peerPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

