package sequencer.retailerSequencer;
import tools.ConfigureManager;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class RetailerSequencer {
	String name;
	ChannelManager channelManager; 
	
	public RetailerSequencer() throws Exception{
		name = "RetailerSequencer";
		String host = ConfigureManager.getInstance().getString("RetailerSequencerHost");
		int port = ConfigureManager.getInstance().getInt("RetailerSequencerPort");
		System.out.println(name + " udp channal:" + host + ":" + port);
		
		ChannelManager channelManager = new ChannelManager();
		
		host = ConfigureManager.getInstance().getString("RetailerFEHost");
		port = ConfigureManager.getInstance().getInt("RetailerFEPort");
		channelManager.addChannel(new Channel(name, "RetailerFE", host, port
				, Group.RetailerFE, new RetailerSequencerMessageProcesser(channelManager)));
		
		host = ConfigureManager.getInstance().getString("RetailerReplica1Host");
		port = ConfigureManager.getInstance().getInt("RetailerReplica1Port");
		channelManager.addChannel(new Channel(name, "RetailerReplica1", host, port
				, Group.RetailerReplica, new RetailerSequencerMessageProcesser(channelManager)));
		
		host = ConfigureManager.getInstance().getString("RetailerReplica2Host");
		port = ConfigureManager.getInstance().getInt("RetailerReplica2Port");
		channelManager.addChannel(new Channel(name, "RetailerReplica2", host, port
				, Group.RetailerReplica, new RetailerSequencerMessageProcesser(channelManager)));
		
		host = ConfigureManager.getInstance().getString("RetailerReplica3Host");
		port = ConfigureManager.getInstance().getInt("RetailerReplica3Port");
		channelManager.addChannel(new Channel(name, "RetailerReplica3", host, port, 
				Group.RetailerReplica, new RetailerSequencerMessageProcesser(channelManager)));
		
		host = ConfigureManager.getInstance().getString("RetailerReplica4Host");
		port = ConfigureManager.getInstance().getInt("RetailerReplica4Port");
		channelManager.addChannel(new Channel(name, "RetailerReplica4", host, port
				, Group.RetailerReplica, new RetailerSequencerMessageProcesser(channelManager)));
	}
	
	public static void main(String []argv){
	
		try {
			String localIp = InetAddress.getLocalHost().getHostAddress();
			String sequencerHost = ConfigureManager.getInstance().getString("RetailerSequencerHost");
			if(!localIp.equals(sequencerHost)){
				System.out.println("Please run the RetailerSequencerHost on:" + sequencerHost + " or change the RetailerSequencerHost of configure file to:" + localIp);
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
