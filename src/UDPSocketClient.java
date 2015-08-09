import java.net.*;

import tools.NetworkIO;
import tools.RetailerFEGetCatelogMessage;
import tools.Channel;
public class UDPSocketClient {
    public static void main(String[] args) {
    	try {
    		Channel channel = new Channel("client", "192.168.12.107", 9876);
    		
			NetworkIO networkIO = new NetworkIO(6789);
			networkIO.sendMsg(new RetailerFEGetCatelogMessage(channel.peerName, channel.localSeq, channel.peerSeq), channel.peerHost, channel.peerPort);
			networkIO.receiveMessage();
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}


