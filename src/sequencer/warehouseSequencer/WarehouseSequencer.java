package sequencer.warehouseSequencer;
import sequencer.warehouseSequencer.WarehouseSequencer;
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
	int index;
	ChannelManager channdelManager;
	LoggerClient loggerClient;
	
	public WarehouseSequencer(int index, LoggerClient loggerClient) throws Exception{
		processName = "Warehouse" + index + "Sequencer";
		this.index = index;
		String host = ConfigureManager.getInstance().getString(processName + "Host");
		int port = ConfigureManager.getInstance().getInt(processName + "Port");
		System.out.println(processName + " udp channel:" + host + ":" + port);
		this.loggerClient = loggerClient;
		
		ChannelManager channelManager = new ChannelManager(port,loggerClient, new WarehouseSequencerMessageProcesser());
		
		host = ConfigureManager.getInstance().getString("Warehouse" + index + "FEHost");
		port = ConfigureManager.getInstance().getInt("Warehouse" + index + "FEPort");
		channelManager.addChannel(new Channel(processName, "Warehouse" + index + "FE", host, port
				, Group.FE));
		
		for(int i = 1; i <=4; i++){
			host = ConfigureManager.getInstance().getString("Warehouse" + index + "Replica" + i + "Host");
			port = ConfigureManager.getInstance().getInt("Warehouse" + index + "Replica" + i + "Port");
			channelManager.addChannel(new Channel(processName, "Warehouse" + index + "Replica" + i, host, port , Group.REPLICA));
		}

		channelManager.start();
	}
	
	public static void main(String []args){
	
		if(args.length == 1)
		{
			int index = Integer.parseInt(args[0]);
			LoggerClient loggerClient = new LoggerClient("Warehouse" + index + "Sequencer");
			try {
				String localIp = InetAddress.getLocalHost().getHostAddress();
				System.out.println(localIp);
				String sequencerHost = ConfigureManager.getInstance().getString("Warehouse" + index + "SequencerHost");
				System.out.println(sequencerHost);
				if(!localIp.equals(sequencerHost)){
					System.out.println("Please run the Warehouse" + index + "SequencerHost on:" + sequencerHost + " or change the Warehouse" + index + "SequencerHost of configure file to:" + localIp);
					return;
				}
				WarehouseSequencer warehouseSequencer = new WarehouseSequencer(index, loggerClient);
				
					
			}catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}else{
			System.out.println("Give a index.");
		}
	}
}
