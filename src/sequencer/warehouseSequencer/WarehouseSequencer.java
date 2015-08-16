package sequencer.warehouseSequencer;
import sequencer.warehouseSequencer.WarehouseSequencerMessageProcesser;
import tools.ConfigureManager;
import tools.LoggerClient;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class WarehouseSequencer {
	String processName;
	ChannelManager channdelManager;
	LoggerClient loggerClient;
	
	public WarehouseSequencer(String warehouseName) throws Exception{
		processName = warehouseName+"Sequencer";
		String host = ConfigureManager.getInstance().getString(warehouseName+"SequencerHost");
		int port = ConfigureManager.getInstance().getInt(warehouseName+"SequencerPort");
		System.out.println(processName + " udp channel:" + host + ":" + port);
		loggerClient = new LoggerClient(processName);
		
		ChannelManager channelManager = new ChannelManager(port,loggerClient, new WarehouseSequencerMessageProcesser());
		
		host = ConfigureManager.getInstance().getString(warehouseName+"FEHost");
		port = ConfigureManager.getInstance().getInt(warehouseName+"FEPort");
		channelManager.addChannel(new Channel(processName, warehouseName+"FE", host, port
				, Group.FE));
		
		for(int i = 1; i <=4; i++){
			host = ConfigureManager.getInstance().getString(warehouseName+"Replica" + i + "Host");
			port = ConfigureManager.getInstance().getInt(warehouseName+"Replica" + i + "Port");
			channelManager.addChannel(new Channel(processName, warehouseName+"Replica" + i, host, port , Group.REPLICA));
		}

		channelManager.start();
		
	}
	
	public static void main(String []argv){
	
		for(int i =0; i<3; i++){
			
			try {
				String localIp = InetAddress.getLocalHost().getHostAddress();
				System.out.println(localIp);
				String sequencerHost = ConfigureManager.getInstance().getString("Warehouse"+(i+1)+"SequencerHost");
				System.out.println(sequencerHost);
				if(!localIp.equals(sequencerHost)){
					System.out.println("Please run the RetailerSequencerHost on:" + sequencerHost + " or change the RetailerSequencerHost of configure file to:" + localIp);
					return;
				}
				WarehouseSequencer warehouseSequencer = new WarehouseSequencer("Warehouse"+(i+1));
				
					
			}catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
