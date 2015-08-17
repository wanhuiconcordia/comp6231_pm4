package manufacturer;

import javax.jws.WebService;

import manufacturer.ManufacturerFEMessageProcesser;
import tools.ConfigureManager;
import tools.Item;
import tools.LoggerClient;
import tools.Product;
import tools.ProductList;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.fe.FE;
import warehouse.WarehouseFEMessageProcesser;
@WebService(endpointInterface = "manufacturer.ManufacturerInterface")
public class ManufacturerFEImpl extends FE implements ManufacturerInterface {
	public LoggerClient loggerClient;
	public String name;
	public int currentSequencerID;
	public Object cachedObj;
	public Object lock = new Object();
	
	/**
	 * Constructor
	 * @param name
	 * @param loggerClient
	 * @throws Exception 
	 */
	public ManufacturerFEImpl(String name , LoggerClient loggerClient) throws Exception {
		this.name = name;
		String fullName = name + "FE";
		this.loggerClient = loggerClient;
		String host = ConfigureManager.getInstance().getString(fullName + "Host");
		int port = ConfigureManager.getInstance().getInt(fullName + "Port");
		System.out.println(fullName + " udp channel:" + host + ":" + port);

		channelManager = new ChannelManager(port, loggerClient, new ManufacturerFEMessageProcesser());

		host = ConfigureManager.getInstance().getString(name + "SequencerHost");
		port = ConfigureManager.getInstance().getInt(name + "SequencerPort");
		channelManager.addChannel(new Channel(name, name + "Sequencer", host, port, Group.SEQUENCER));

		for(int i = 1; i <= 4; i++){
			host = ConfigureManager.getInstance().getString(name + "Replica" + i + "Host");
			port = ConfigureManager.getInstance().getInt(name + "Replica" + i + "Port");
			channelManager.addChannel(new Channel(name, name + "Replica" + i, host, port, Group.REPLICA));

			host = ConfigureManager.getInstance().getString(name + "RM" + i + "Host");
			port = ConfigureManager.getInstance().getInt(name + "RM" + i + "Port");
			channelManager.addChannel(new Channel(name, name + "RM" + i, host, port, Group.RM));
		}
		
		channelManager.start();
	}

	@Override
	public String processPurchaseOrder(Item item, int sequencerID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Product getProductInfo(String aProdName, int sequencerID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean receivePayment(String orderNum, float totalPrice,
			int sequencerID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ProductList getProductList(int sequencerID) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
