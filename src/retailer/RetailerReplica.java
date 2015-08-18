package retailer;

import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import tools.ConfigureManager;
import tools.LoggerClient;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.message.Message;
import tools.message.Packet;
import tools.message.replica.AskInitSyncMessage;
import tools.message.replica.AskSyncMessage;
import tools.message.replica.InitMessage;
import warehouse.WarehouseInterface;

public class RetailerReplica {
	public String baseName;
	public String fullName;
	public int replicaIndex;
	public int mode;	
	public ChannelManager channelManager; 
	public LoggerClient loggerClient;
	
	public ArrayList<WarehouseInterface> warehouseFEList = new ArrayList<WarehouseInterface>();
	public CustomerManager customerManager;
	
	public RetailerReplica(LoggerClient loggerClient, int replicaIndex, int mode, String goodReplicaName) throws Exception{
		baseName = "RetailerReplica";
		fullName = baseName + replicaIndex;
		this.replicaIndex = replicaIndex;
		this.mode = mode;
		this.loggerClient = loggerClient; 
		String host = ConfigureManager.getInstance().getString("RetailerReplica" + replicaIndex + "Host");
		int port = ConfigureManager.getInstance().getInt("RetailerReplica" + replicaIndex + "Port");
		System.out.println(fullName + " udp channel:" + host + ":" + port);
		loggerClient.write(fullName + " udp channel:" + host + ":" + port);

		ChannelManager channelManager = new ChannelManager(port, loggerClient, new RetailerReplicaMessageProcesser(this));

		for(int i = 1; i <= 4; i++){
			if(i != replicaIndex){
				host = ConfigureManager.getInstance().getString(baseName + i + "Host");
				port = ConfigureManager.getInstance().getInt(baseName + i + "Port");
				channelManager.addChannel(new Channel(fullName, baseName + i, host, port, Group.REPLICA));
			}
		}

		host = ConfigureManager.getInstance().getString("RetailerSequencerHost");
		port = ConfigureManager.getInstance().getInt("RetailerSequencerPort");
		channelManager.addChannel(new Channel(fullName, "RetailerSequencer", host, port, Group.SEQUENCER));
		
		host = ConfigureManager.getInstance().getString("RetailerRM" + replicaIndex + "Host");
		port = ConfigureManager.getInstance().getInt("RetailerRM" + replicaIndex + "Port");
		channelManager.addChannel(new Channel(fullName, "RetailerRM" + replicaIndex, host, port , Group.RM));

		host = ConfigureManager.getInstance().getString("RetailerFEHost");
		port = ConfigureManager.getInstance().getInt("RetailerFEPort");
		channelManager.addChannel(new Channel(fullName, "RetailerFE", host, port, Group.FE));

		customerManager = new CustomerManager(fullName + "Customers.xml");
		
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

	public boolean connectWarehouseFE() throws Exception{
		int copies = ConfigureManager.getInstance().getInt("copies");
		try {		
			for(int i = 1; i<= copies; i++){
				String warehouseFEHost = ConfigureManager.getInstance().getString("Warehouse" + i + "FEHost");
				String warehouseFEServicePort = ConfigureManager.getInstance().getString("Warehouse" + i + "FEServicePort");
				URL url = new URL("http://" + warehouseFEHost + ":" + warehouseFEServicePort + "/ws/Warehouse" + i + "FE?wsdl");
				QName qname = new QName("http://warehouse/", "WarehouseFEImplService");
				Service service = Service.create(url, qname);
				warehouseFEList.add(service.getPort(WarehouseInterface.class));
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void main(String[] args) {
		String baseName = "RetailerReplica";
		String paraOptions = "Wrong parameters. 3 parameters are expected. Para 1 is for RetailerReplica index(1-4). Para 2 is for start mode(0-1). Para 3 is for goodReplicaName.";
		LoggerClient loggerClient = new LoggerClient(baseName);
		if(args.length == 3){
			try{
				int index = Integer.parseInt(args[0]);
				int mode = Integer.parseInt(args[1]);
				String goodReplicaName = args[2];
				if(index > 0 
						&& index < 5 
						&& (mode == 0 
						|| mode == 1)){
					String fullName = baseName + index;
					loggerClient.setSenderName(fullName);
					try {
						String localIp = InetAddress.getLocalHost().getHostAddress();
						String configHost = ConfigureManager.getInstance().getString(fullName + "Host");
						if(localIp.equals(configHost)){
							RetailerReplica retailerReplica = new RetailerReplica(loggerClient, index, mode, goodReplicaName);
							if(!retailerReplica.connectWarehouseFE()){
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

