package sequencer.retailerSequencer;
import tools.ConfigureManager;
import tools.LoggerClient;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class RetailerSequencer {
	String name;
	ChannelManager channelManager; 
	LoggerClient loggerClient;
	
	public RetailerSequencer() throws Exception{
		name = "RetailerSequencer";
		String host = ConfigureManager.getInstance().getString("RetailerSequencerHost");
		int port = ConfigureManager.getInstance().getInt("RetailerSequencerPort");
		System.out.println(name + " udp channel:" + host + ":" + port);
		loggerClient = new LoggerClient(name);
		
		ChannelManager channelManager = new ChannelManager(port, loggerClient, new RetailerSequencerMessageProcesser());
		
		host = ConfigureManager.getInstance().getString("RetailerFEHost");
		port = ConfigureManager.getInstance().getInt("RetailerFEPort");
		channelManager.addChannel(new Channel(name, "RetailerFE", host, port, Group.FE));
		
		for(int i = 1; i <=4; i++){
			host = ConfigureManager.getInstance().getString("RetailerReplica" + i + "Host");
			port = ConfigureManager.getInstance().getInt("RetailerReplica" + i + "Port");
			channelManager.addChannel(new Channel(name, "RetailerReplica" + i, host, port , Group.REPLICA));
		}

		channelManager.start();
	}
	
	public static void main(String []argv){
	
		try {
			String localIp = InetAddress.getLocalHost().getHostAddress();
			String hostOfConfig = ConfigureManager.getInstance().getString("RetailerSequencerHost");
			if(!localIp.equals(hostOfConfig)){
				System.out.println("Please run the RetailerSequencer on:" + hostOfConfig + " or change the RetailerSequencerHost of configure file to:" + localIp);
				return;
			}
			RetailerSequencer retailerSequencer = new RetailerSequencer();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
