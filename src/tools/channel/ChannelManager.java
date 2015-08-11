package tools.channel;

import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import tools.ConfigureManager;
import tools.message.Message;
import tools.message.Packet;

public class ChannelManager{
	public HashMap<String, Channel> channelMap;
	public Object outgoingPacketQueueLock;
	public Queue<Packet> outgoingPacketQueue;
	NetworkIO networkIO;
	ReadThread readThread;
	WriteThread writeThread;
	public ChannelManager() throws SocketException, Exception {
		channelMap = new HashMap<String, Channel>();
		outgoingPacketQueueLock = new Object();
		outgoingPacketQueue = new LinkedList<Packet>();
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
			System.out.println("Udp channel to " + channel.peerProcessName + ":" + channel.peerHost + ":" + channel.peerPort);
		}
	}
	
	public void processMessage(Message message) {
		if(channelMap.containsKey(message.sender)){
			channelMap.get(message.sender).messageProcesser.processMessage(message);
		}else{
			System.out.println("channelMap does not contian " + message.sender);
		}
	}
	
	public void collectLostPacket(){
		for(Channel channel: channelMap.values()){
			if(channel.hasCachedMsg){
				outgoingPacketQueue.add(new Packet(channel.peerHost, channel.peerPort, channel.cachedMsg));
			}
		}
	}
}
