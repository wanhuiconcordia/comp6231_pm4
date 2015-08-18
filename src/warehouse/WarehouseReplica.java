package warehouse;

import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import manufacturer.ManufacturerInterface;
import retailer.RetailerInterface;
import tools.ConfigureManager;
import tools.Item;
import tools.LoggerClient;
import tools.Product;
import tools.ProductList;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.message.Message;
import tools.message.Packet;
import tools.message.replica.AskInitSyncMessage;
import tools.message.replica.InitMessage;

public class WarehouseReplica {
	
	public String baseName;
	public String extraName;
	public String fullName;
	public int warehouseIndex;
	public int replicaIndex;
	public int mode;
	public String goodReplicaName;
	public ChannelManager channelManager; 
	public LoggerClient loggerClient;
	public InventoryManager inventoryManager;
	public ArrayList<ManufacturerInterface> manufacturerFEList = new ArrayList<ManufacturerInterface>();
	
	public WarehouseReplica(LoggerClient loggerClient 
			, String baseName
			, String extraName
			, int warehouseIndex
			, int replicaIndex
			, int mode
			, String goodReplicaName) throws Exception{
		this.loggerClient = loggerClient;
		this.baseName = baseName;
		this.extraName = extraName;
		this.warehouseIndex = warehouseIndex;
		this.replicaIndex = replicaIndex;
		this.mode = mode;
		this.fullName = baseName + warehouseIndex + extraName + replicaIndex;
		this.goodReplicaName = goodReplicaName;
		
		String host = ConfigureManager.getInstance().getString(fullName + "Host");
		int port = ConfigureManager.getInstance().getInt(fullName + "Port");
		System.out.println(fullName + " udp channel:" + host + ":" + port);
		loggerClient.write(fullName + " udp channel:" + host + ":" + port);
		
		ChannelManager channelManager = new ChannelManager(port, loggerClient, new WarehouseReplicaMessageProcesser(this));
		
		
		for(int i = 1; i <= 4; i++){
			if(i != replicaIndex){
				host = ConfigureManager.getInstance().getString(baseName + warehouseIndex + extraName + i + "Host");
				port = ConfigureManager.getInstance().getInt(baseName + warehouseIndex + extraName + i + "Port");
				channelManager.addChannel(new Channel(fullName, baseName + warehouseIndex + extraName + i, host, port, Group.REPLICA));
			}
		}

		host = ConfigureManager.getInstance().getString(baseName + warehouseIndex + "SequencerHost");
		port = ConfigureManager.getInstance().getInt(baseName + warehouseIndex + "SequencerPort");
		channelManager.addChannel(new Channel(fullName, baseName + warehouseIndex + "Sequencer", host, port, Group.SEQUENCER));
		
		host = ConfigureManager.getInstance().getString(baseName + warehouseIndex + "RM" + replicaIndex + "Host");
		port = ConfigureManager.getInstance().getInt(baseName + warehouseIndex + "RM" + replicaIndex + "Port");
		channelManager.addChannel(new Channel(fullName, baseName + warehouseIndex + "RM" + replicaIndex, host, port , Group.RM));

		host = ConfigureManager.getInstance().getString(baseName + warehouseIndex + "FEHost");
		port = ConfigureManager.getInstance().getInt(baseName + warehouseIndex + "FEPort");
		channelManager.addChannel(new Channel(fullName, baseName + warehouseIndex + "FE", host, port, Group.FE));

		
		channelManager.start();
		
		inventoryManager= new InventoryManager(fullName);		
		
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
	
	public boolean connectManufacturerFE(){
		try {		
			int copies = ConfigureManager.getInstance().getInt("copies");
			for(int i = 1; i<= copies; i++){
				String manufactuerFEHost = ConfigureManager.getInstance().getString("Manufacturer" + i + "FEHost");
				String manufacturerFEServicePort = ConfigureManager.getInstance().getString("Manufacturer" + i + "FEServicePort");
				URL url = new URL("http://" + manufactuerFEHost + ":" + manufacturerFEServicePort + "/ws/Manufacturer" + i + "FE?wsdl");
				QName qname = new QName("http://manufacturer/", "ManufacturerFEImplService");
				Service service = Service.create(url, qname);
				manufacturerFEList.add(service.getPort(ManufacturerInterface.class));
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void main(String[] args) {
		String baseName = "Warehouse";
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
				int warehouseIndex = Integer.parseInt(args[0]);
				int replicaIndex = Integer.parseInt(args[1]);
				int mode = Integer.parseInt(args[2]);
				String goodReplicaName = args[3];
				
				if(warehouseIndex > 0 
						&& warehouseIndex < 4 
						&& replicaIndex > 0 
						&& replicaIndex < 5 
						&& mode >=0 
						&& mode <=1){
					fullName = baseName + warehouseIndex + extraName + replicaIndex;
					loggerClient.setSenderName(fullName);
					try {
						String localIp = InetAddress.getLocalHost().getHostAddress();
						String configHost = ConfigureManager.getInstance().getString(fullName + "Host");
						if(localIp.equals(configHost)){
							WarehouseReplica warehouseReplica = new WarehouseReplica(loggerClient
									, baseName
									, extraName
									, warehouseIndex
									, replicaIndex
									, mode
									, goodReplicaName);
							if(!warehouseReplica.connectManufacturerFE()){
								System.out.println("Failed to connect warehouseFEs");
								loggerClient.write("Failed to connect warehouseFEs");
								return;
							}
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
