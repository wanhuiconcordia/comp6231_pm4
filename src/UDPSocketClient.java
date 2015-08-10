import java.net.*;

import tools.Item;
import tools.ItemList;
import tools.Product;
import tools.channel.Channel;
import tools.channel.NetworkIO;
import tools.message.RetailerFEGetCatelogMessage;
import tools.message.RetailerFESignInMessage;
import tools.message.TestMessage;
public class UDPSocketClient {
    public static void main(String[] args) {
    	try {
    		Channel channel = new Channel("client", "192.168.12.109", 9876);
    		
			NetworkIO networkIO = new NetworkIO(6789);
//			networkIO.sendMsg(new RetailerFEGetCatelogMessage(channel.peerName, channel.localSeq, channel.peerSeq), channel.peerHost, channel.peerPort);
//			networkIO.sendMsg(new RetailerFESignInMessage(channel.peerName, channel.localSeq, channel.peerSeq, 122, "password123"), channel.peerHost, channel.peerPort);
//			networkIO.sendMsg(new TestMessage(channel.peerName, channel.localSeq, channel.peerSeq, new Product("Sony", "TV", (float) 120.0)), channel.peerHost, channel.peerPort);
			
			
			ItemList itemList = new ItemList();
			itemList.addItem(new Item("Sony", "TV", (float)120.0, 5));
			itemList.addItem(new Item("Sony", "DVD", (float)220.0, 15));
			itemList.addItem(new Item("Sony", "Camara", (float)320.0, 25));
			
			networkIO.sendMsg(new TestMessage(channel.peerProcessName, channel.localSeq, channel.peerSeq, itemList), channel.peerHost, channel.peerPort);
			networkIO.receiveMessage();
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}


