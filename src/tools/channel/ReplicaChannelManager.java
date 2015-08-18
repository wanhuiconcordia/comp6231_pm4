package tools.channel;

import java.net.SocketException;

import tools.LoggerClient;
import tools.message.MessageProcesser;

public class ReplicaChannelManager extends ChannelManager {
	public String goodReplicaName; 
	public ReplicaChannelManager(int localPort, LoggerClient loggerClient,
			MessageProcesser messageProcesser) throws SocketException,
			Exception {
		super(localPort, loggerClient, messageProcesser);
		// TODO Auto-generated constructor stub
	}

}
