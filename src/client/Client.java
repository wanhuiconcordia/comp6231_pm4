package client;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPFaultException;

import tools.*;
import retailer.*;

/**
 * @author comp6231.team5
 * Client side application
 * Functionalities: 
 * #sign in
 * #sign up
 * #sign out
 * #get item information
 * #make orders
 */
public class Client
{
	private RetailerInterface retailerFE;	
	private Scanner in;
	private Customer currentCustomer;
	private LoggerClient loggerClient;
	private String name;
	private ArrayList<Item> retailerItemCatalog;


	
	/**
	 * Constructor
	 */
	public Client(){
		name = "client";
		in = new Scanner(System.in);
		loggerClient = new LoggerClient(name);
		retailerItemCatalog = new ArrayList<Item>();
	}
	
	/**
	 * Provide a interface for user to input the retailer's port for connecting
	 * @return
	 */
	public boolean connectRetailerFE(){
		try {		
			String retailerFEHost = ConfigureManager.getInstance().getString("RetailerFEHost");
			String RetailerFEServicePort = ConfigureManager.getInstance().getString("RetailerFEServicePort");
			URL url = new URL("http://" + retailerFEHost + ":" + RetailerFEServicePort + "/ws/retailerFE?wsdl");
			QName qname = new QName("http://retailer/", "RetailerFEImplService");
			Service service = Service.create(url, qname);
			retailerFE = service.getPort(RetailerInterface.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Sign up
	 * @return sign up result
	 */
	public boolean customerSignUp(){
		if(currentCustomer != null){
			customerSignOut();
		}
		System.out.print("Input customer name:");
		String name = in.next();
		System.out.print("Input customer password:");
		String password = in.next();
		System.out.print("Input customer street1:");
		String street1 = in.next();
		System.out.print("Input customer street2:");
		String street2 = in.next();
		System.out.print("Input customer city:");
		String city = in.next();
		System.out.print("Input customer state:");
		String state = in.next();
		System.out.print("Input customer zip code:");
		String zip = in.next();
		System.out.print("Input customer country:");
		String country = in.next();
		try {
			loggerClient.write(this.name + ": Tries to sign up with:" + name + ", " + password
					+ ", " + street1
					+ ", " + street2
					+ ", " + city
					+ ", " + state
					+ ", " + zip
					+ ", " + country);
			SignUpResult signUpResult  = retailerFE.signUp(name, password, street1, street2, city, state, zip, country);
			if(signUpResult.result){
				currentCustomer = new Customer(signUpResult.customerReferenceNumber, name, password, street1, street2, city, state, zip, country);
				System.out.println("Your creferenceNumber is:" + signUpResult.customerReferenceNumber);
				loggerClient.write(this.name + ": Sign successfully. The retailer returned:" + signUpResult.message);
			}else{
				System.out.println("Failed to sign up. The retailer returned:" + signUpResult.message);
				loggerClient.write(this.name + ": Failed to sign up. The retailer returned:" + signUpResult.message);
			}
			return signUpResult.result;
		}catch(SOAPFaultException e){
			System.out.println("Failed to sign up. SOAPFaultException: rpc return null.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Sign in
	 * @return sign in result
	 */
	public boolean customerSignIn(){
		if(currentCustomer != null){
			customerSignOut();
		}
		try{
			System.out.print("Input customer ReferenceNumber:");
			int customerReferenceNumber = Integer.parseInt(in.next());
			System.out.print("Input customer password:");
			String password = in.next();

			loggerClient.write(name + ": Tries to sign in with reference number:" + customerReferenceNumber + " and password:" + password);
			currentCustomer = retailerFE.signIn(customerReferenceNumber, password);

			System.out.println("Signed in properly. Your person informations are:" + currentCustomer.toString());
			loggerClient.write(name + ": Signed in properly. The customer info:" + currentCustomer.toString());
			return true;
			
		}catch (NumberFormatException e){
			System.out.println("ReferenceNumber should contains digits only. Please try again.");
		}catch(SOAPFaultException e){
			System.out.println("Failed to sign in. SOAPFaultException: rpc return null.");
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Sing out
	 */
	public void customerSignOut(){
		if(currentCustomer == null)
			return;
		loggerClient.write(name + ": Current customer:" + currentCustomer.name + " signed out.");
		currentCustomer = null;
	}

	/**
	 * get retailer's item catalog
	 * @return available item list
	 */
	public void getCatalog(){
		if(currentCustomer == null){
			System.out.println("This operation is only allowed for registed user. Please sign in or sign up.");
		}else{
			try{
				ItemList itemList = retailerFE.getCatalog(currentCustomer.customerReferenceNumber);
				retailerItemCatalog.clear();
				System.out.println("Retailer item catalog:");
				System.out.println(itemList.toString());
				retailerItemCatalog = itemList.innerItemList;
			}catch(SOAPFaultException e){
				System.out.println("Failed to get catalog. SOAPFaultException: RPC return null.");
				//e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * make order
	 * @return the items shipping status 
	 */
	public void makeOrder(){
		if(currentCustomer == null){
			System.out.println("This operation is only allowed for registed user. Please sign in or sign up.");
		}else{
			
			if(retailerItemCatalog.isEmpty()){
				getCatalog();
			}
			
			String inputString = new String();
			ArrayList<Item> itemOrderList = new ArrayList<Item>();
			String log = new String();
			for(Item item: retailerItemCatalog){
				System.out.print("Input quantity for :" + item.toString() + " (q for finishing order list):");
				inputString = in.next();
				if(inputString.equals("q")){
					break;
				}else{
					try{
						int quantiy = Integer.parseInt(inputString);
						Item tmpItem = item.clone();
						tmpItem.quantity = quantiy;
						itemOrderList.add(tmpItem);
						log = log + tmpItem.toString() + "</br>";
					}catch(NumberFormatException e){
						System.out.println("Please input a number or 'q'.");
					}
				}
			}
			if(itemOrderList.size() > 0){
				loggerClient.write(name + ": Tries to order:");
				loggerClient.write(log);
				
				ItemList itemList = new ItemList();
				itemList.setItems(itemOrderList);
				try{
				ItemShippingStatusList itemShippingStatusList = retailerFE.submitOrder(currentCustomer.customerReferenceNumber, itemList);
				System.out.println("Return itemShippingStatus:");
				System.out.println(itemShippingStatusList.toString());
				}catch(SOAPFaultException e){
					System.out.println("Failed to submitOrder. SOAPFaultException: RPC return null.");
				}
			}
		}
	}

	public static void main(String args[])
	{
		Client client = new Client();
		
		if(!client.connectRetailerFE()){
			return;
		}
		String operation;
		do{
			System.out.println("Type [1] to sign up.");
			System.out.println("Type [2] to sign in.");
			System.out.println("Type [3] to sign out.");
			System.out.println("Type [4] to get product catalog.");
			System.out.println("Type [5] to make an order.");
			System.out.println("Type [6] to quit.");
			System.out.print("Please input:");
			operation = client.in.next();
			if(operation.compareTo("1") == 0){
				client.customerSignUp();
			}else if(operation.compareTo("2") == 0){
				client.customerSignIn();
			}else if(operation.compareTo("3") == 0){
				client.customerSignOut();
			}else if(operation.compareTo("4") == 0){
				client.getCatalog();
			}else if(operation.compareTo("5") == 0){
				client.makeOrder();
			}else if(operation.compareTo("6") == 0){
				break;
			}else{
				System.out.println("Wrong input. Try again.");
			}
		}while(true);

		client.in.close();
	}
}
