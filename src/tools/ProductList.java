package tools;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author comp6231.team5
 *
 */
public class ProductList implements Serializable{
	public ArrayList<Product> innerProductList;
	
	/**
	 * Constructor 
	 */
	public ProductList(){
		innerProductList = new ArrayList<Product>();
	}
	
	/**
	 * Constructor 
	 * @param Product Product
	 */
	public void addProduct(Product Product){
		if(innerProductList == null){
			innerProductList = new ArrayList<Product>();
		}
		innerProductList.add(Product);
	}
	
	/**
	 * @param ArrayList<Product> otherProductList
	 */
	public void setProducts(ArrayList<Product> otherProductList){
		innerProductList = otherProductList;
	}
	
	/**
	 * clear all the elements in innerProductList.
	 */
	public void clearProducts(){
		innerProductList.clear();
	}
	
	public String toString(){
		String retStr = new String();
		if(innerProductList != null){
			for(Product product: innerProductList){
				retStr += (product.toString() + "\n");
			}
		}
		return retStr;
	}
}
