package tools;
/**
 * @author comp6231.team5
 *
 */
public class ItemShippingStatus extends Item{
	public boolean shippingStatus;
	
	/**
	 * Default constructor
	 */
	public ItemShippingStatus() {
		super();
		this.shippingStatus = false;
	}
	/**
	 * constructor
	 * @param Item item
	 * @param bool shippingStatus
	 */
	public ItemShippingStatus(Item item, boolean shippingStatus) {
		super(item);
		this.shippingStatus = shippingStatus;
	}
	
	/**
	 * constructor
	 * @param String manufacturerName
	 * @param String productType
	 * @param float unitPrice
	 * @param int quantity
	 * @param bool shippingStatus
	 */
	public ItemShippingStatus(String manufacturerName, 
			String productType,
			float unitPrice,
			int quantity, 
			boolean shippingStatus){
		super(manufacturerName, productType, unitPrice, quantity);
		this.shippingStatus = shippingStatus;
	}
	
	/**
	 * Constructor
	 * @param String productID 
	 * @param String manufacturerName
	 * @param String productType
	 * @param float unitPrice
	 * @param int quantity
	 * @param bool shippingStatus
	 */
	public ItemShippingStatus(String productID,
			String manufacturerName,
			String productType,
			float unitPrice,
			int quantity,
			boolean shippingStatus
			){
		super(productID, manufacturerName, productType, unitPrice, quantity);
		this.shippingStatus = shippingStatus;
	}

	/**
	 * Constructor
	 * @param ItemShippingStatus itemShippingStatus
	 */
	public ItemShippingStatus(ItemShippingStatus itemShippingStatus){
		super(itemShippingStatus.productID
				, itemShippingStatus.manufacturerName
				, itemShippingStatus.productType
				, itemShippingStatus.unitPrice
				, itemShippingStatus.quantity);
		this.shippingStatus = itemShippingStatus.shippingStatus;
	}
	
	/* (non-Javadoc)
	 * @see tools.Item#toString()
	 */
	public String toString(){
		return super.toString() + ",\t" + (shippingStatus ? "shipped" : "not shipped"); 
	}
}
