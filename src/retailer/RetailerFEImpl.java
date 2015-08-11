package retailer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import retailer.CustomerManager;
import tools.ConfigureManager;
import tools.Customer;
import tools.Item;
import tools.ItemList;
import tools.ItemShippingStatus;
import tools.ItemShippingStatusList;
import tools.LoggerClient;
import tools.SignUpResult;
//import warehouse.WarehouseInterface;
import tools.channel.Channel;
import tools.channel.ChannelManager;
import tools.channel.Group;

@WebService(endpointInterface = "retailer.RetailerInterface")
public class RetailerFEImpl implements RetailerInterface {
	public CustomerManager customerManager;
	public LoggerClient loggerClient;
	//private ArrayList<WarehouseInterface> warehouseList;
	public String name;
	ChannelManager channelManager; 	
	/**
	 * Constructor
	 * @param name
	 */
	public RetailerFEImpl(String name) throws Exception{
		this.name = name;
//		warehouseList = new ArrayList<WarehouseInterface>();
		//this.loggerClient = new LoggerClient();
		customerManager = new CustomerManager("customers.xml");
		loggerClient = new LoggerClient(name);
		//this.connectWarehouses();
		String host = ConfigureManager.getInstance().getString("RetailerFEHost");
		int port = ConfigureManager.getInstance().getInt("RetailerFEPort");
		System.out.println(name + " udp channel:" + host + ":" + port);
		
		ChannelManager channelManager = new ChannelManager(port, loggerClient, new RetailerFEMessageProcesser());
		
		host = ConfigureManager.getInstance().getString("RetailerSequencerHost");
		port = ConfigureManager.getInstance().getInt("RetailerSequencerPort");
		channelManager.addChannel(new Channel(name, "RetailerSequencer", host, port, Group.RetailerSequencer));
		
		host = ConfigureManager.getInstance().getString("RetailerReplica1Host");
		port = ConfigureManager.getInstance().getInt("RetailerReplica1Port");
		channelManager.addChannel(new Channel(name, "RetailerReplica1", host, port, Group.RetailerReplica));
		
		
		host = ConfigureManager.getInstance().getString("RetailerReplica2Host");
		port = ConfigureManager.getInstance().getInt("RetailerReplica2Port");
		channelManager.addChannel(new Channel(name, "RetailerReplica2", host, port, Group.RetailerReplica));
		
		host = ConfigureManager.getInstance().getString("RetailerReplica3Host");
		port = ConfigureManager.getInstance().getInt("RetailerReplica3Port");
		channelManager.addChannel(new Channel(name, "RetailerReplica3", host, port, Group.RetailerReplica));
		
		host = ConfigureManager.getInstance().getString("RetailerReplica4Host");
		port = ConfigureManager.getInstance().getInt("RetailerReplica4Port");
		channelManager.addChannel(new Channel(name, "RetailerReplica4", host, port, Group.RetailerReplica));
		
		host = ConfigureManager.getInstance().getString("RetailerRM1Host");
		port = ConfigureManager.getInstance().getInt("RetailerRM1Port");
		channelManager.addChannel(new Channel(name, "RetailerRM1", host, port, Group.RetailerRM));
		
		host = ConfigureManager.getInstance().getString("RetailerRM2Host");
		port = ConfigureManager.getInstance().getInt("RetailerRM2Port");
		channelManager.addChannel(new Channel(name, "RetailerRM2", host, port, Group.RetailerRM));
		
		host = ConfigureManager.getInstance().getString("RetailerRM3Host");
		port = ConfigureManager.getInstance().getInt("RetailerRM3Port");
		channelManager.addChannel(new Channel(name, "RetailerRM3", host, port, Group.RetailerRM));
		
		host = ConfigureManager.getInstance().getString("RetailerRM4Host");
		port = ConfigureManager.getInstance().getInt("RetailerRM4Port");
		channelManager.addChannel(new Channel(name, "RetailerRM4", host, port, Group.RetailerRM));
		channelManager.start();
	}

	
	/**
	 * Provide interface for user to input the warehouses' ports for connecting
	 */
//	public void connectWarehouses(){
//		Scanner in = new Scanner(System.in);
//		while(true){
//			System.out.print("Please input the port number of the warehouse service to establish connection (q to finish):");
//			
//			String port = in.nextLine();
//			if(port.equals("q")){
//				break;
//			}else{
//				String urlStr = "http://localhost:" + port + "/ws/warehouse?wsdl";
//				try {
//					URL url = new URL(urlStr);
//					QName qname = new QName("http://warehouse/", "WarehouseImplService");
//					WarehouseInterface warehouse;
//					Service service = Service.create(url, qname);
//					warehouse = service.getPort(WarehouseInterface.class);
//					System.out.println("Obtained a handle on server object: " + warehouse.getName());
//					warehouseList.add(warehouse);
//				}catch (MalformedURLException e1) {
//					e1.printStackTrace();
//				}catch (Exception e) {
//					//System.out.println("Failed to access the WSDL at:" + urlStr);
//					e.printStackTrace();
//					return;
//				}
//			}
//		}	
//		in.close();
//	}

	/* (non-Javadoc)
	 * @see retailer.RetailerInterface#getCatalog(int)
	 */
	@Override
	public ItemList getCatalog(int customerReferenceNumber) {
		System.out.println("getCatalog is called...");
//		ItemList itemList = new ItemList();
//		HashMap<String, Item> itemsMap = new HashMap<String, Item>();
//
//		for(int i = 0; i < warehouseList.size(); i++){
//			ItemList itemListFromWarehouse = warehouseList.get(i).getProductsByID("");
//			for(Item item: itemListFromWarehouse.innerItemList){
//				String key = item.productID;
//				Item itemInMap = itemsMap.get(key); 
//				if(itemInMap == null){
//					itemsMap.put(key, item.clone());
//				}else{
//					itemInMap.quantity= itemInMap.quantity + item.quantity;
//				}
//			}
//		}
//
//		for(Item item: itemsMap.values()){
//			itemList.innerItemList.add(item);
//		}
//		System.out.println(itemList.toString());
//		return itemList;
		
		
		
		ItemList itemList = new ItemList();
		itemList.addItem(new Item("Sony", "DVD", (float)120, 5));
		itemList.addItem(new Item("Sony", "TV", (float)220, 15));
		itemList.addItem(new Item("Samsung", "Cell phone", (float)320, 25));
		itemList.addItem(new Item("Apple", "Cell phone", (float)420, 20));
		return itemList;
	}

	/* (non-Javadoc)
	 * @see retailer.RetailerInterface#submitOrder(int, tools.ItemList)
	 */
	@Override
	public synchronized ItemShippingStatusList submitOrder(int customerReferenceNumber,
			ItemList itemOrderList) {
		System.out.println("ItemShippingStatusList is called...");
//		ItemShippingStatusList itemShippingStatusList= new ItemShippingStatusList();
//		Customer currentCustomer = customerManager.getCustomerByReferenceNumber(customerReferenceNumber);
//		if(currentCustomer == null){
//			loggerClient.write(name + ": customer reference number can not be found in customer database.");
//			return itemShippingStatusList;
//		}
//		
//		if(itemOrderList == null){
//			loggerClient.write(name + ": null order list.");
//			return itemShippingStatusList;
//		}else if(itemOrderList.innerItemList.isEmpty()){
//			loggerClient.write(name + ": empty order list.");
//			return itemShippingStatusList;
//		}else{
//			HashMap<String, ItemShippingStatus> receivedItemShippingStatusMap = new HashMap<String, ItemShippingStatus>();
//			HashMap<String, Item> orderMap = new HashMap<String, Item>();
//			for(Item item: itemOrderList.innerItemList){
//				Item itemImpl = new Item(item);
//				System.out.println("item orderd"+itemImpl.toString());
//				if(itemImpl.quantity > 0){
//					Item itemInOrderMap = orderMap.get(itemImpl.productID);
//					if(itemInOrderMap == null){
//						orderMap.put(item.productID, new Item(itemImpl));
//					}else{
//						itemInOrderMap.quantity += itemImpl.quantity;
//					}
//				}
//			}
//			System.out.println("order map:" +orderMap);
//			
//			for(WarehouseInterface thisWarehouse: warehouseList){
//				int itemRequestFromWarehouseCount = orderMap.size();
//				
//				if(itemRequestFromWarehouseCount > 0)
//				{
//					ItemList itemRequestFromWarehouseList = new ItemList(itemRequestFromWarehouseCount);
//					System.out.println("itemRequestFromWarehouseList size : "+ itemRequestFromWarehouseList.innerItemList.size());
//					int i = 0;
//					for(Item orderItem: orderMap.values()){
//						System.out.println("orderItem: "+ orderItem);
//						itemRequestFromWarehouseList.innerItemList.add(i, orderItem);
//						i++;
//					}
//					System.out.println("itemRequestFromWarehouseList size after adding: "+ itemRequestFromWarehouseList.innerItemList.size());
//					ItemList itemsGotFromCurrentWarehouse=null;
//					if(thisWarehouse.registerRetailer(name)){
//					
//						itemsGotFromCurrentWarehouse = thisWarehouse.shippingGoods(itemRequestFromWarehouseList, name);
//					}
//					if(itemsGotFromCurrentWarehouse == null){
//						System.out.println("warehouse return null");
//					}else if(itemsGotFromCurrentWarehouse.innerItemList.isEmpty()){
//						System.out.println("warehouse return empty arrry");
//					}else{
//						String log = new String();
//						for(Item item: itemsGotFromCurrentWarehouse.innerItemList){
//							Item itemInReceivedItemShippingStatusMap = receivedItemShippingStatusMap.get(item.productID);
//							if(itemInReceivedItemShippingStatusMap == null){
//								receivedItemShippingStatusMap.put(item.productID, new ItemShippingStatus(item, true));
//							}else{
//								itemInReceivedItemShippingStatusMap.quantity += item.quantity;
//							}
//
//							Item itemInOrderMap = orderMap.get(item.productID);
//							if(itemInOrderMap == null){
//								System.out.println("Warehouse side error. never request this item from warehouse, but the warehouse return this item.");
//							}else{
//								itemInOrderMap.quantity -= item.quantity;
//								if(itemInOrderMap.quantity == 0){
//									orderMap.remove(item.productID);
//								}
//							}
//						}
//					}
//				}else{
//					break;
//				}
//			}
//			
//			ArrayList<ItemShippingStatus> tmpItemShippingStatusList = new ArrayList<ItemShippingStatus>();
//			
//			for(ItemShippingStatus itemInReceivedItemShippingStatusMap: receivedItemShippingStatusMap.values()){
//				tmpItemShippingStatusList.add(itemInReceivedItemShippingStatusMap);
//			}
//			
//			for(Item itemInOrderMap: orderMap.values()){
//				tmpItemShippingStatusList.add(new ItemShippingStatus(itemInOrderMap, false));
//			}
//			
//			itemShippingStatusList.setItems(tmpItemShippingStatusList);
//			return itemShippingStatusList;
//		}
		return null;
	}

	/* (non-Javadoc)
	 * @see retailer.RetailerInterface#signUp(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public SignUpResult signUp(String name, String password, String street1,
			String street2, String city, String state, String zip,
			String country) {
		System.out.println("signUp is called...");
		return customerManager.register(name, password, street1, street2, city, state, zip, country);
	}

	/* (non-Javadoc)
	 * @see retailer.RetailerInterface#signIn(int, java.lang.String)
	 */
	@Override
	public Customer signIn(int customerReferenceNumber, String password) {
		System.out.println("signIn is called...");
		return customerManager.find(customerReferenceNumber, password);
	}
}

