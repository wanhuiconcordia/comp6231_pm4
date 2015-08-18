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
	 * registerRetailer
	 * @param sequencerID
	 * @return true if success otherwise return false;
	 */
	@WebMethod ItemList shippingGoods (ItemList itemList, int sequencerID);
}