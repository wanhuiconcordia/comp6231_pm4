package tools;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

/**
 * 
 * @author comp6231.team5
 *
 */
public class Item extends Product{
	public int quantity;
	
	public Item(){
		super();
	}
	/**
	 * constructor
	 * @param Item otherItem
	 */
	public Item(Item otherItem){
		super(otherItem.productID, otherItem.manufacturerName, otherItem.productType, otherItem.unitPrice);
		this.quantity = otherItem.quantity;
	}
	/**
	 * @param String manufacturerName
	 * @param String productType
	 * @param float unitPrice
	 * @param int quantity
	 */
	public Item(String manufacturerName, String productType, float unitPrice, int quantity){
		super(manufacturerName, productType, unitPrice);
		this.quantity = quantity;
	}
	
	/**
	 * @param String productID
	 * @param String manufacturerName
	 * @param String productType
	 * @param float unitPrice
	 * @param int quantity
	 */
	public Item(String productID, String manufacturerName, String productType, float unitPrice, int quantity){
		super(productID, manufacturerName, productType, unitPrice);
		this.quantity = quantity;
	}
	
	/**
	 * constructor
	 * @param Product product
	 * @param int quantity
	 */
	public Item(Product product, int quantity){
		super(product);
		this.quantity = quantity;
	}
	
	/* (non-Javadoc)
	 * @see tools.Product#toString()
	 */
	public String toString(){
		return super.toString() + ", " + quantity; 
	}
	
	@Override
	public Item clone(){
		return new Item(this);
	}
	
	/**
	 * @return the clone product from the current item 
	 */
	public Product cloneProduct(){
		return super.clone();
	}
	
	/**
	 * determine whether the current item is the same as the other item
	 * @param Item otherItem
	 * @return ture if the same, false if not
	 */
	public boolean isSameProductAs(Item otherItem){
		return super.isSame(otherItem);
	}

	public boolean isSame(Item other){
		return super.isSame(other)
				&& quantity == other.quantity;
	}
	
	/**
	 * save the current item to an  Element object
	 * @return the Element object
	 */
	public Element toXmlElement() {
		DefaultElement customerElem = new DefaultElement("item");
		
		Element subElem = customerElem.addElement("manufacturerName");
		subElem.setText(manufacturerName);
		
		subElem = customerElem.addElement("productType");
		subElem.setText(productType);
		
		subElem = customerElem.addElement("unitPrice");
		subElem.setText(String.valueOf(unitPrice));
		
		subElem = customerElem.addElement("quantity");
		subElem.setText(String.valueOf(quantity));
		return customerElem;
	}
}
