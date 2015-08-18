package manufacturer;

import java.net.InetAddress;
import java.util.HashMap;

import tools.ConfigureManager;
import tools.Item;
import tools.LoggerClient;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.message.Message;
import tools.message.Packet;
import tools.message.replica.AskInitSyncMessage;
import tools.message.replica.InitMessage;

public class ManufacturerReplica {
	
	public String baseName;
	public String extraName;
	public String fullName;
	public int manufacturerIndex;
	public int replicaIndex;
	public int mode;
	public String goodReplicaName;
	public ChannelManager channelManager; 
	public LoggerClient loggerClient;

	
	private HashMap<String, Item> purchaseOrderMap;
	private int orderNum;
	private PurchaseOrderManager purchaseOrderManager;
	
	public ManufacturerReplica(LoggerClient loggerClient 
			, String baseName
			, String extraName
			, int manufacturerIndex
			, int replicaIndex
			, int mode
			, String goodReplicaName) throws Exception{
		this.loggerClient = loggerClient;
		this.baseName = baseName;
		this.extraName = extraName;
		this.manufacturerIndex = manufacturerIndex;
		this.replicaIndex = replicaIndex;
		this.mode = mode;
		this.fullName = baseName + manufacturerIndex + extraName + replicaIndex;
		this.goodReplicaName = goodReplicaName;
		
		String host = ConfigureManager.getInstance().getString(fullName + "Host");
		int port = ConfigureManager.getInstance().getInt(fullName + "Port");
		System.out.println(fullName + " udp channel:" + host + ":" + port);
		loggerClient.write(fullName + " udp channel:" + host + ":" + port);
		
		ChannelManager channelManager = new ChannelManager(port, loggerClient, new ManufacturerReplicaMessageProcesser(this));
		
		
		for(int i = 1; i <= 4; i++){
			if(i != replicaIndex){
				host = ConfigureManager.getInstance().getString(baseName + manufacturerIndex + extraName + i + "Host");
				port = ConfigureManager.getInstance().getInt(baseName + manufacturerIndex + extraName + i + "Port");
				channelManager.addChannel(new Channel(fullName, baseName + manufacturerIndex + extraName + i, host, port, Group.REPLICA));
			}
		}

		host = ConfigureManager.getInstance().getString(baseName + manufacturerIndex + "SequencerHost");
		port = ConfigureManager.getInstance().getInt(baseName + manufacturerIndex + "SequencerPort");
		channelManager.addChannel(new Channel(fullName, baseName + manufacturerIndex + "Sequencer", host, port, Group.SEQUENCER));
		
		host = ConfigureManager.getInstance().getString(baseName + manufacturerIndex + "RM" + replicaIndex + "Host");
		port = ConfigureManager.getInstance().getInt(baseName + manufacturerIndex + "RM" + replicaIndex + "Port");
		channelManager.addChannel(new Channel(fullName, baseName + manufacturerIndex + "RM" + replicaIndex, host, port , Group.RM));

		host = ConfigureManager.getInstance().getString(baseName + manufacturerIndex + "FEHost");
		port = ConfigureManager.getInstance().getInt(baseName + manufacturerIndex + "FEPort");
		channelManager.addChannel(new Channel(fullName, baseName + manufacturerIndex + "FE", host, port, Group.FE));

		
		channelManager.start();
		
		if(mode == 1){
			for(Channel channel: channelManager.channelMap.values()){
				if(channel.group == Group.RM){
					//DO NOTHING
				}else if(channel.group == Group.REPLICA
					&& channel.peerProcessName.equals(goodReplicaName)){
					Message msg = new AskInitSyncMessage(channel.localProcessName
							, ++channel.localSeq
							, channel.peerSeq);
					channel.backupPacket = new Packet(channel.peerProcessName, channel.peerHost
							, channel.peerPort
							, msg);
					channel.isWaitingForRespose = true;
				}else{
					Message msg = new InitMessage(channel.localProcessName
									, ++channel.localSeq
									, channel.peerSeq);
					channel.backupPacket = new Packet(channel.peerProcessName
							, channel.peerHost
							, channel.peerPort
							, msg);
					channel.isWaitingForRespose = true;
				}
			}
		}
		
		
		
		
	}
	public static void main(String[] args) {
		String baseName = "Manufacturer";
		String extraName = "Replica";
		String fullName = baseName + extraName;
		String paraOptions = "Wrong parameters. 4 parameters are expected."
				+ "Para 1 is the warehouse index(1-3). "
				+ "Para 2 is the Replica index(1-4). "
				+ "Para 3 is for start mode(0-1). "
				+ "Para 4 is for goodReplicaName.";
		LoggerClient loggerClient = new LoggerClient(fullName);
		if(args.length == 4){
			try{
				int manufacturerIndex = Integer.parseInt(args[0]);
				int replicaIndex = Integer.parseInt(args[1]);
				int mode = Integer.parseInt(args[2]);
				String goodReplicaName = args[3];
				
				if(manufacturerIndex > 0 
						&& manufacturerIndex < 4 
						&& replicaIndex > 0 
						&& replicaIndex < 5 
						&& mode >=0 
						&& mode <=1){
					fullName = baseName + manufacturerIndex + extraName + replicaIndex;
					loggerClient.setSenderName(fullName);
					try {
						String localIp = InetAddress.getLocalHost().getHostAddress();
						String configHost = ConfigureManager.getInstance().getString(fullName + "Host");
						if(localIp.equals(configHost)){
							ManufacturerReplica retailerReplica = new ManufacturerReplica(loggerClient
									, baseName
									, extraName
									, manufacturerIndex
									, replicaIndex
									, mode
									, goodReplicaName);
						}else{
							System.out.println("Please run the " + fullName + " on:" 
									+ configHost + " or change the " + fullName + "Host of configure file to:" + localIp);

							loggerClient.write("Please run the " + fullName +" on:" 
									+ configHost + " or change the " + fullName + "Host of configure file to:" + localIp);
						}
					} catch (Exception e1) {
						e1.printStackTrace();
						loggerClient.write(e1.toString());
					}
				}else{
					System.out.println(paraOptions);
					loggerClient.write(paraOptions);	
				}
				
			}catch(NumberFormatException e){
				System.out.println(paraOptions);
				loggerClient.write(paraOptions);
			}
		}else{
			System.out.println(paraOptions);
			loggerClient.write(paraOptions);
		}
	}
}
