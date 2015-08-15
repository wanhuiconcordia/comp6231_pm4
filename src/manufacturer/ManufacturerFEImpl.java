package manufacturer;

import manufacturer.ManufacturerFEMessageProcesser;
import tools.ConfigureManager;
import tools.Item;
import tools.LoggerClient;
import tools.Product;
import tools.ProductList;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;

public class ManufacturerFEImpl implements ManufacturerInterface {
	public LoggerClient loggerClient;
	public String name;
	ChannelManager channelManager; 
	
	/**
	 * Constructor
	 * @param name
	 * @param loggerClient
	 * @throws Exception 
	 */
	public ManufacturerFEImpl(String name , LoggerClient loggerClient) throws Exception {
		this.name = name;
		this.loggerClient = loggerClient;
		String host = ConfigureManager.getInstance().getString("ManufacturerFEHost");
		int port = ConfigureManager.getInstance().getInt("ManufacturerFEPort");
		System.out.println(name + " udp channel:" + host + ":" + port);

		channelManager = new ChannelManager(port, loggerClient, new ManufacturerFEMessageProcesser());

		host = ConfigureManager.getInstance().getString("ManufacturerSequencerHost");
		port = ConfigureManager.getInstance().getInt("ManufacturerSequencerPort");
		channelManager.addChannel(new Channel(name, "ManufacturerSequencer", host, port, Group.SEQUENCER));

		for(int i = 1; i <= 4; i++){
			host = ConfigureManager.getInstance().getString("ManufacturerReplica" + i + "Host");
			port = ConfigureManager.getInstance().getInt("ManufacturerReplica" + i + "Port");
			channelManager.addChannel(new Channel(name, "ManufacturerReplica" + i, host, port, Group.REPLICA));

			host = ConfigureManager.getInstance().getString("ManufacturerRM" + i + "Host");
			port = ConfigureManager.getInstance().getInt("ManufacturerRM" + i + "Port");
			channelManager.addChannel(new Channel(name, "ManufacturerRM" + i, host, port, Group.RM));
		}

		channelManager.start();
	}
	@Override
	public String processPurchaseOrder(Item item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Product getProductInfo(String aProdName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean receivePayment(String orderNum, float totalPrice) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ProductList getProductList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
