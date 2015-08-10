package tools;

import java.util.HashMap;

public class ChannelManager implements MessageProcesser{
	HashMap<String, Channel> channelMap;
	public ChannelManager() {
		channelMap = new HashMap<String, Channel>();
	}
	
	public void addChannel(Channel channel){
		if(channelMap.containsKey(channel.peerProcessName)){
			System.out.println(channel.peerProcessName + " already exists in channelMap!");
		}else{
			channelMap.put(channel.peerHost, channel);
			System.out.println("Udp channal to " + channel.peerProcessName + ":" + channel.peerHost + ":" + channel.peerPort);
		}
	}
	@Override
	public void processMessage(Message message) {
		if(channelMap.containsKey(message.sender)){
			channelMap.get(message.sender).processMessage(message);
		}else{
			System.out.println("channelMap does not contian " + message.sender);
		}
	}

}
