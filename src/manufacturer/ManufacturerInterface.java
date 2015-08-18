package manufacturer;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import tools.Customer;
import tools.Item;
import tools.ItemList;
import tools.ItemShippingStatus;
import tools.ItemShippingStatusList;
import tools.Product;
import tools.ProductList;
import tools.SignUpResult;

//Service Endpoint Interface
@WebService
@SOAPBinding(style = Style.RPC)

public interface ManufacturerInterface {
	/**
	 * get processing order number
	 * @param item
	 * @param sequencerID
	 * @return OrderNo
	 */
	@WebMethod boolean processPurchaseOrder (ItemList itemList, int sequencerID);
	/**
	 * get ProductInfo
	 * @param aProdName
	 * @param sequencerID
	 * @return Product
	 */
	@WebMethod Product getProductInfo (String aProdName, int sequencerID);
	/**
	 * Check payment received info
	 * @param orderNum
	 * @param totalPrice
	 * @param sequencerID
	 * @return boolean
	 */
	@WebMethod boolean receivePayment (String orderNum, float totalPrice, int sequencerID);
	/**
	 * get product list
	 * @param sequencerID
	 * @return ProductList
	 */
	@WebMethod ProductList getProductList(int sequencerID);
}