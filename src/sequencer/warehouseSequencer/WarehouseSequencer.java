package sequencer.warehouseSequencer;
import sequencer.retailerSequencer.RetailerSequencerMessageProcesser;
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
		
		ChannelManager channelManager = new ChannelManager(port,loggerClient,new WarehouseSequencerMessageProcesser());
		
		host = ConfigureManager.getInstance().getString(warehouseName+"FEHost");
		port = ConfigureManager.getInstance().getInt(warehouseName+"FEPort");
		channelManager.addChannel(new Channel(processName, warehouseName+"FE", host, port
				, Group.WarehouseFE));
		
		host = ConfigureManager.getInstance().getString(warehouseName+"Replica1Host");
		port = ConfigureManager.getInstance().getInt(warehouseName+"Replica1Port");
		channelManager.addChannel(new Channel(processName, warehouseName+"Replica1", host, port
				, Group.WarehouseReplica));
		
		host = ConfigureManager.getInstance().getString(warehouseName+"Replica2Host");
		port = ConfigureManager.getInstance().getInt(warehouseName+"Replica2Port");
		channelManager.addChannel(new Channel(processName, warehouseName+"Replica2", host, port
				, Group.WarehouseReplica));
		
		host = ConfigureManager.getInstance().getString(warehouseName+"Replica3Host");
		port = ConfigureManager.getInstance().getInt(warehouseName+"Replica3Port");
		channelManager.addChannel(new Channel(processName, warehouseName+"Replica3", host, port, 
				Group.WarehouseReplica));
		
		host = ConfigureManager.getInstance().getString(warehouseName+"Replica4Host");
		port = ConfigureManager.getInstance().getInt(warehouseName+"Replica4Port");
		channelManager.addChannel(new Channel(processName, warehouseName+"Replica4", host, port
				, Group.WarehouseReplica));
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
