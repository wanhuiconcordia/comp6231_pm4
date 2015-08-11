package rm;

import tools.ConfigureManager;
import tools.LoggerClient;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;

public class RetailerRM {
	String name;
	int index;
	ChannelManager channelManager; 
	LoggerClient loggerClient;
	public RetailerRM(int index) throws Exception{
		
		this.name = "RetailerRM";
		loggerClient = new LoggerClient(name + index);
		this.index = index;
		String host = ConfigureManager.getInstance().getString(name + index + "Host");
		int port = ConfigureManager.getInstance().getInt(name + index + "Port");
		System.out.println(name + " udp channel:" + host + ":" + port);
		loggerClient.write(name + " udp channel:" + host + ":" + port);
		
		ChannelManager channelManager = new ChannelManager(loggerClient, new RetailerRMMessageProcesser());
		
		for(int i = 1; i <= 4; i++){
			if(i != index){
				host = ConfigureManager.getInstance().getString(name + i + "Host");
				port = ConfigureManager.getInstance().getInt(name + i + "Port");
				channelManager.addChannel(new Channel(name + index, name + i, host, port, Group.RetailerRM));
			}
		}
		
		host = ConfigureManager.getInstance().getString("RetailerFEHost");
		port = ConfigureManager.getInstance().getInt("RetailerFEPort");
		channelManager.addChannel(new Channel(name + index, "RetailerFE", host, port, Group.RetailerFE));
		
		host = ConfigureManager.getInstance().getString("RetailerReplica" + index + "Host");
		port = ConfigureManager.getInstance().getInt("RetailerReplica" + index + "Port");
		channelManager.addChannel(new Channel(name + index, "RetailerReplica" + index, host, port, Group.RetailerReplica));
		Runtime.getRuntime().exec("java retailer.RetailerReplica " + index + " INIT");
		channelManager.start();
	}
}
