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
	public ItemList(int i){
		innerItemList = new ArrayList<Item>(i);
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
	
	public boolean isSame(ItemList other){
		if(innerItemList.size() != other.innerItemList.size()){
			return false;
		}else{
			for(int i = 0; i < innerItemList.size() - 1; i++){	//Bad! not efficient.
				if(!innerItemList.get(i).isSame(other.innerItemList.get(i))){
					return false;
				}
			}
			return true;
		}
	}
}
