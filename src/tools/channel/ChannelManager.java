package tools.channel;

import java.net.SocketException;
import java.util.HashMap;

import tools.ConfigureManager;
import tools.message.Message;

public class ChannelManager{
	public HashMap<String, Channel> channelMap;
	NetworkIO networkIO;
	ReadThread readThread;
	WriteThread writeThread;
	public ChannelManager() throws SocketException, Exception {
		channelMap = new HashMap<String, Channel>();
		networkIO = new NetworkIO(ConfigureManager.getInstance().getInt("RetailerSequencerPort"));
		readThread = new ReadThread(this, networkIO);
		writeThread = new WriteThread(this, networkIO);
		readThread.start();
		writeThread.start();
	}
	
	public void addChannel(Channel channel){
		if(channelMap.containsKey(channel.peerProcessName)){
			System.out.println(channel.peerProcessName + " already exists in channelMap!");
		}else{
			channelMap.put(channel.peerHost, channel);
			System.out.println("Udp channal to " + channel.peerProcessName + ":" + channel.peerHost + ":" + channel.peerPort);
		}
	}
	
	public void processMessage(Message message) {
		if(channelMap.containsKey(message.sender)){
			channelMap.get(message.sender).processMessage(message);
		}else{
			System.out.println("channelMap does not contian " + message.sender);
		}
	}
}
