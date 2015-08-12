package manufacturerSequencer;
import sequencer.retailerSequencer.RetailerSequencerMessageProcesser;
import tools.ConfigureManager;
import tools.LoggerClient;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ManufacturerSequencer {
	String processName;
	ChannelManager channdelManager;
	LoggerClient loggerClient;
	
	public ManufacturerSequencer(String manufacturerName) throws Exception{
		processName = manufacturerName+"Sequencer";
		String host = ConfigureManager.getInstance().getString(manufacturerName+"SequencerHost");
		int port = ConfigureManager.getInstance().getInt(manufacturerName+"SequencerPort");
		System.out.println(processName + " udp channel:" + host + ":" + port);
		loggerClient = new LoggerClient(processName);
		
		ChannelManager channelManager = new ChannelManager(port,loggerClient,new ManufacturerSequencerMessageProcesser());
		
		host = ConfigureManager.getInstance().getString(manufacturerName+"FEHost");
		port = ConfigureManager.getInstance().getInt(manufacturerName+"FEPort");
		channelManager.addChannel(new Channel(processName, manufacturerName+"FE", host, port
				, Group.ManufacturerFE));
		
		host = ConfigureManager.getInstance().getString(manufacturerName+"Replica1Host");
		port = ConfigureManager.getInstance().getInt(manufacturerName+"Replica1Port");
		channelManager.addChannel(new Channel(processName, manufacturerName+"Replica1", host, port
				, Group.ManufacturerReplica));
		
		host = ConfigureManager.getInstance().getString(manufacturerName+"Replica2Host");
		port = ConfigureManager.getInstance().getInt(manufacturerName+"Replica2Port");
		channelManager.addChannel(new Channel(processName, manufacturerName+"Replica2", host, port
				, Group.ManufacturerReplica));
		
		host = ConfigureManager.getInstance().getString(manufacturerName+"Replica3Host");
		port = ConfigureManager.getInstance().getInt(manufacturerName+"Replica3Port");
		channelManager.addChannel(new Channel(processName, manufacturerName+"Replica3", host, port, 
				Group.ManufacturerReplica));
		
		host = ConfigureManager.getInstance().getString(manufacturerName+"Replica4Host");
		port = ConfigureManager.getInstance().getInt(manufacturerName+"Replica4Port");
		channelManager.addChannel(new Channel(processName, manufacturerName+"Replica4", host, port
				, Group.ManufacturerReplica));
	}
	
	public static void main(String []argv){
	
		for(int i =0; i<3; i++){
			
			try {
				String localIp = InetAddress.getLocalHost().getHostAddress();
				System.out.println(localIp);
				String sequencerHost = ConfigureManager.getInstance().getString("manufacturer"+(i+1)+"SequencerHost");
				System.out.println(sequencerHost);
				if(!localIp.equals(sequencerHost)){
					System.out.println("Please run the RetailerSequencerHost on:" + sequencerHost + " or change the RetailerSequencerHost of configure file to:" + localIp);
					return;
				}
				ManufacturerSequencer warehouseSequencer = new ManufacturerSequencer("manufacturer"+(i+1));
				
					
			}catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
