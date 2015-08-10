package sequencer;
import tools.Channel;
import tools.ChannelManager;
import tools.ConfigureManager;
import tools.Message;
import tools.MessageProcesser;
import tools.NetworkIO;
import tools.RetailerFEToRetailerSequencerChannel;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

public class RetailerSequencer {
	String name;
	HashMap<String, Channel> channelMap;
	
	ChannelManager channelManager = new ChannelManager(); 
	ReadThread readThread;
	NetworkIO networkIO;
	
	public RetailerSequencer() throws Exception{
		name = "RetailerSequencer";
		channelMap = new HashMap<String, Channel>();

		String host = ConfigureManager.getInstance().getString("RetailerSequencerHost");
		int port = ConfigureManager.getInstance().getInt("RetailerSequencerPort");
		System.out.println(name + " udp channal:" + host + ":" + port);
		
		host = ConfigureManager.getInstance().getString("RetailerFEHost");
		port = ConfigureManager.getInstance().getInt("RetailerFEPort");
		channelManager.addChannel(new RetailerFEToRetailerSequencerChannel(name, "RetailerFE", host, port));
		
		host = ConfigureManager.getInstance().getString("RetailerReplica1Host");
		port = ConfigureManager.getInstance().getInt("RetailerReplica1Port");
		channelManager.addChannel(new RetailerFEToRetailerSequencerChannel(name, "RetailerReplica1", host, port));
		
		host = ConfigureManager.getInstance().getString("RetailerReplica2Host");
		port = ConfigureManager.getInstance().getInt("RetailerReplica2Port");
		channelManager.addChannel(new RetailerFEToRetailerSequencerChannel(name, "RetailerReplica2", host, port));
		
		host = ConfigureManager.getInstance().getString("RetailerReplica3Host");
		port = ConfigureManager.getInstance().getInt("RetailerReplica3Port");
		channelManager.addChannel(new RetailerFEToRetailerSequencerChannel(name, "RetailerReplica3", host, port));
		
		host = ConfigureManager.getInstance().getString("RetailerReplica4Host");
		port = ConfigureManager.getInstance().getInt("RetailerReplica4Port");
		channelManager.addChannel(new RetailerFEToRetailerSequencerChannel(name, "RetailerReplica4", host, port));

		networkIO = new NetworkIO(ConfigureManager.getInstance().getInt("RetailerSequencerPort"));
		
		readThread = new ReadThread(channelManager, networkIO);
		readThread.start();
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

class ReadThread extends Thread{
	ChannelManager channelManager;
	NetworkIO networkIO;
	boolean keepReading;
	public ReadThread(ChannelManager channelManager, NetworkIO networkIO){
		this.channelManager = channelManager;
		this.networkIO = networkIO;
		keepReading = true;
	}

	public void run(){
		while(keepReading){
			
			Message msg = networkIO.receiveMessage();
			if(msg != null){
				channelManager.processMessage(msg);
			}
		}
	}
	
	public void terminate(){
		keepReading = false;
	}
}
