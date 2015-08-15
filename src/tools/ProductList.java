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
	
	@Override
	public String toString(){
		String retStr = new String();
		if(innerProductList != null){
			for(Product product: innerProductList){
				retStr += (product.toString() + "\n");
			}
		}
		return retStr;
	}
	
	public boolean isSame(ProductList other){
		if(innerProductList.size() != other.innerProductList.size()){
			return false;
		}else{
			for(int i = 0; i < innerProductList.size() - 1; i++){	//Bad! not efficient.
				if(!innerProductList.get(i).isSame(other.innerProductList.get(i))){
					return false;
				}
			}
			return true;
		}
	}
}
