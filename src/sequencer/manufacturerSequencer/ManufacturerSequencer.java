package sequencer.manufacturerSequencer;
import sequencer.manufacturerSequencer.ManufacturerSequencerMessageProcesser;
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
		
		ChannelManager channelManager = new ChannelManager(port,loggerClient, new ManufacturerSequencerMessageProcesser());
		
		host = ConfigureManager.getInstance().getString(manufacturerName+"FEHost");
		port = ConfigureManager.getInstance().getInt(manufacturerName+"FEPort");
		channelManager.addChannel(new Channel(processName, manufacturerName+"FE", host, port
				, Group.FE));
		
		for(int i = 1; i <=4; i++){
			host = ConfigureManager.getInstance().getString(manufacturerName+"Replica" + i + "Host");
			port = ConfigureManager.getInstance().getInt(manufacturerName+"Replica" + i + "Port");
			channelManager.addChannel(new Channel(processName, manufacturerName+"Replica" + i, host, port , Group.REPLICA));
		}

		channelManager.start();
	}
	
	public static void main(String []argv){
	
		for(int i =0; i<3; i++){
			
			try {
				String localIp = InetAddress.getLocalHost().getHostAddress();
				System.out.println(localIp);
				String sequencerHost = ConfigureManager.getInstance().getString("Manufacturer"+(i+1)+"SequencerHost");
				System.out.println(sequencerHost);
				if(!localIp.equals(sequencerHost)){
					System.out.println("Please run the RetailerSequencerHost on:" + sequencerHost + " or change the RetailerSequencerHost of configure file to:" + localIp);
					return;
				}
				ManufacturerSequencer manufacturerSequencer = new ManufacturerSequencer("Manufacturer"+(i+1));
				
					
			}catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
