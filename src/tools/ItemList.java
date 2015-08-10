package tools;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author comp6231.team5
 *
 */
public class ItemList implements Serializable{
	public ArrayList<Item> innerItemList;
	
	/**
	 * Constructor 
	 */
	public ItemList(){
		innerItemList = new ArrayList<Item>();
	}
	
	/**
	 * Constructor 
	 * @param Item item
	 */
	public void addItem(Item item){
		if(innerItemList == null){
			innerItemList = new ArrayList<Item>();
		}
		innerItemList.add(item);
	}
	
	/**
	 * @param ArrayList<Item> otherItemList
	 */
	public void setItems(ArrayList<Item> otherItemList){
		innerItemList = otherItemList;
	}
	
	/**
	 * clear all the elements in innerItemList.
	 */
	public void clearItems(){
		innerItemList.clear();
	}
	
	public String toString(){
		String retStr = new String();
		if(innerItemList != null){
			for(Item item: innerItemList){
				retStr += (item.toString() + "\n");
			}
		}
		return retStr;
	}
}
