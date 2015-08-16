package warehouse;

import javax.jws.WebService;

import tools.ConfigureManager;
import tools.ItemList;
import tools.LoggerClient;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;
import tools.fe.FE;

@WebService(endpointInterface = "warehouse.WarehouseInterface")
public class WarehouseFEImpl extends FE implements WarehouseInterface {
	public LoggerClient loggerClient;
	public String name;
	/**
	 * Constructor
	 * @param name
	 */
	public WarehouseFEImpl(String name, LoggerClient loggerClient) throws Exception{
		this.name = name;
		String fullName = name + "FE";
		this.loggerClient = loggerClient;
		String host = ConfigureManager.getInstance().getString(fullName + "Host");
		int port = ConfigureManager.getInstance().getInt(fullName + "Port");
		System.out.println(fullName + " udp channel:" + host + ":" + port);

		channelManager = new ChannelManager(port, loggerClient, new WarehouseFEMessageProcesser());

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
	public synchronized ItemList getProductsByID(String productID) {
		
		return null;
	}

	@Override
	public ItemList getProductsByType(String productType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemList getProductsByRegisteredManufacturers(String manufacturerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemList getProducts(String productID, String manufacturerName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean registerRetailer(String retailerName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unregisterRegailer(String retailerName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ItemList shippingGoods(ItemList itemList, String reatilername) {
		// TODO Auto-generated method stub
		return null;
	}
}