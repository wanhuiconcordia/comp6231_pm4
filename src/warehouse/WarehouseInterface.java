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
	 * @param sequencerID
	 * @return ItemList
	 */
	@WebMethod ItemList getProductsByID (String productID, int sequencerID);
	
	/**
	 * get products by type
	 * @param productType
	 * @param sequencerID
	 * @return ItemList
	 */
	@WebMethod ItemList getProductsByType (String productType, int sequencerID);
	/**
	 * get products by ManufacturerName
	 * @param manufacturerName
	 * @param sequencerID
	 * @return ItemList
	 */
	@WebMethod ItemList getProductsByRegisteredManufacturers (String manufacturerName, int sequencerID);
	/**
	 * get products by ManufacturerName and product ID
	 * @param manufacturerName
	 * @param productID
	 * @param sequencerID
	 * @return ItemList
	 */
	@WebMethod ItemList getProducts (String productID, String manufacturerName, int sequencerID);
	/**
	 * registerRetailer
	 * @param retailerName
	 * @param sequencerID
	 * @return true if success otherwise return false;
	 */
	boolean registerRetailer (String retailerName, int sequencerID);
	/**
	 * unregisterRegailer
	 * @param retailerName
	 * @param sequencerID
	 * @return true if success otherwise return false;
	 */
	boolean unregisterRegailer (String retailerName, int sequencerID);
	/**
	 * shippingGoods
	 * @param itemList
	 * @param reatilername
	 * @param sequencerID
	 * @return ItemList
	 */
	@WebMethod ItemList shippingGoods (ItemList itemList,String reatilername, int sequencerID);
}