package retailer;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import tools.Customer;
import tools.Item;
import tools.ItemList;
import tools.ItemShippingStatus;
import tools.ItemShippingStatusList;
import tools.SignUpResult;

//Service Endpoint Interface
@WebService
@SOAPBinding(style = Style.RPC)

public interface RetailerInterface {
	/**
	 * get item catalogs
	 * @param customerReferenceNumber
	 * @return ItemList
	 */
	@WebMethod ItemList getCatalog (int customerReferenceNumber);
	/**
	 * Process client orders
	 * @param customerReferenceNumber
	 * @param orderItemList
	 * @return ItemShippingStatusList
	 */
	@WebMethod ItemShippingStatusList submitOrder (int customerReferenceNumber, ItemList orderItemList);
	/**
	 * Process customer sign up
	 * @param name
	 * @param password
	 * @param street1
	 * @param street2
	 * @param city
	 * @param state
	 * @param zip
	 * @param country
	 * @return SignUpResult
	 */
	@WebMethod SignUpResult signUp (String name, String password, String street1, String street2, String city, String state, String zip, String country);
	/**
	 * Process client sign in
	 * @param customerReferenceNumber
	 * @param password
	 * @return Customer
	 */
	@WebMethod Customer signIn (int customerReferenceNumber, String password);
}