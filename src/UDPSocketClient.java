import java.net.*;

import tools.Item;
import tools.ItemList;
import tools.channel.Channel;
import tools.channel.Group;
import tools.channel.NetworkIO;
import tools.message.TestMessage;
public class UDPSocketClient {
    public static void main(String[] args) throws SocketTimeoutException {
    	try {
    		Channel channel = new Channel("UDPSocketServer", "UDPSocketClient", "localhost", 9876, Group.RetailerFE, null);
			NetworkIO networkIO = new NetworkIO(6789);

			ItemList itemList = new ItemList();
			itemList.addItem(new Item("Sony", "TV", (float)120.0, 5));
			itemList.addItem(new Item("Sony", "DVD", (float)220.0, 15));
			itemList.addItem(new Item("Sony", "Camara", (float)320.0, 25));
			
			networkIO.sendMsg(new TestMessage(channel.peerProcessName, channel.localSeq, channel.peerSeq, itemList), channel.peerHost, channel.peerPort);
			while(true){
				try {
					networkIO.receiveMessage();
					break;
				} catch (SocketTimeoutException e) {
					//time out 
					//e.printStackTrace();
				}
			}
			
		} catch (SocketException e) {
			e.printStackTrace();
		}
    }
}


