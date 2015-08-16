package warehouse;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import tools.ItemList;

//Service Endpoint Interface
@WebService
@SOAPBinding(style = Style.RPC)

public interface WarehouseInterface {
	/**
	 * get products by ID
	 * @param productID
	 * @return ItemList
	 */
	@WebMethod ItemList getProductsByID (String productID);
	
	/**
	 * get products by type
	 * @param productType
	 * @return ItemList
	 */
	@WebMethod ItemList getProductsByType (String productType);
	/**
	 * get products by ManufacturerName
	 * @param manufacturerName
	 * @return ItemList
	 */
	@WebMethod ItemList getProductsByRegisteredManufacturers (String manufacturerName);
	/**
	 * get products by ManufacturerName and product ID
	 * @param manufacturerName
	 * @param productID
	 * @return ItemList
	 */
	@WebMethod ItemList getProducts (String productID, String manufacturerName);
	/**
	 * registerRetailer
	 * @param retailerName
	 * @return true if success otherwise return false;
	 */
	boolean registerRetailer (String retailerName);
	/**
	 * unregisterRegailer
	 * @param retailerName
	 * @return true if success otherwise return false;
	 */
	boolean unregisterRegailer (String retailerName);
	/**
	 * shippingGoods
	 * @param itemList
	 * @param reatilername
	 * @return ItemList
	 */
	@WebMethod ItemList shippingGoods (ItemList itemList,String reatilername);
}