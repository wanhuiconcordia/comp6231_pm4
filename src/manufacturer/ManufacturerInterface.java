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
	 * @return OrderNo
	 */
	@WebMethod String processPurchaseOrder (Item item);
	/**
	 * get ProductInfo
	 * @param aProdName
	 * @return Product
	 */
	@WebMethod Product getProductInfo (String aProdName);
	/**
	 * Check payment received info
	 * @param orderNum
	 * @param totalPrice
	 * @return boolean
	 */
	@WebMethod boolean receivePayment (String orderNum, float totalPrice);
	/**
	 * get product list
	 * @return ProductList
	 */
	@WebMethod ProductList getProductList();
	/**
	 * get manufacturer name
	 * @return String name
	 */
	@WebMethod String getName();
}