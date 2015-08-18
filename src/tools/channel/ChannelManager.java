package tools.channel;

import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import sequencer.retailerSequencer.RetailerSequencerMessageProcesser;
import tools.ConfigureManager;
import tools.LoggerClient;
import tools.message.Message;
import tools.message.MessageProcesser;
import tools.message.Packet;

public class ChannelManager{
	public LoggerClient loggerClient;
	public HashMap<String, Channel> channelMap;
	public Object outgoingPacketQueueLock;
	public Queue<Packet> outgoingPacketQueue;
	public MessageProcesser messageProcesser;
	public int sequencerID;
	NetworkIO networkIO;
	ReadThread readThread;
	WriteThread writeThread;
	public ChannelManager(int localPort, LoggerClient loggerClient, MessageProcesser messageProcesser) throws SocketException, Exception {
		this.loggerClient = loggerClient;
		this.messageProcesser = messageProcesser;
		channelMap = new HashMap<String, Channel>();
		outgoingPacketQueueLock = new Object();
		outgoingPacketQueue = new LinkedList<Packet>();
		sequencerID = 0;
		networkIO = new NetworkIO(localPort);
		readThread = new ReadThread(this, networkIO);
		writeThread = new WriteThread(this, networkIO);
	}
	
	public void addChannel(Channel channel){
		if(channelMap.containsKey(channel.peerProcessName)){
			System.out.println(channel.peerProcessName + " already exists in channelMap!");
		}else{
			channelMap.put(channel.peerProcessName, channel);
			System.out.println("Udp channel is added." + channel.toString());
		}
	}
	
	public void processMessage(Message message) {
		messageProcesser.processMessage(this, message);
	}
	
	public void processTimeout(){
		messageProcesser.processTimeout(this);
	}
	
	public void start(){
		readThread.start();
		writeThread.start();
	}
}
