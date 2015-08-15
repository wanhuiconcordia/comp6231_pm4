package tools;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author comp6231.team5
 *
 */
public class ItemShippingStatusList implements Serializable{
	public ArrayList<ItemShippingStatus> innerItemShippingStatusList;
	
	/**
	 * Constructor 
	 */
	public ItemShippingStatusList(){
		innerItemShippingStatusList = new ArrayList<ItemShippingStatus>();
	}
	
	/**
	 * Constructor 
	 * @param Item item
	 */
	public void addItem(ItemShippingStatus item){
		if(innerItemShippingStatusList == null){
			innerItemShippingStatusList = new ArrayList<ItemShippingStatus>();
		}
		innerItemShippingStatusList.add(item);
	}
	
	/**
	 * @param ArrayList<Item> otherItemShippingStatusList
	 */
	public void setItems(ArrayList<ItemShippingStatus> otherItemShippingStatusList){
		innerItemShippingStatusList = otherItemShippingStatusList;
	}
	
	/**
	 * clear all the elements in innerItemShippingStatusList.
	 */
	public void clearItems(){
		innerItemShippingStatusList.clear();
	}
	
	public String toString(){
		String retStr = new String();
		if(innerItemShippingStatusList != null){
			for(ItemShippingStatus itemShippingStatus: innerItemShippingStatusList){
				retStr += (itemShippingStatus.toString() + "\n");
			}
		}
		return retStr;
	}
	
	public boolean isSame(ItemShippingStatusList other){
		if(innerItemShippingStatusList.size() != other.innerItemShippingStatusList.size()){
			return false;
		}else{
			for(int i = 0; i < innerItemShippingStatusList.size() - 1; i++){	//Bad! not efficient.
				if(!innerItemShippingStatusList.get(i).isSame(other.innerItemShippingStatusList.get(i))){
					return false;
				}
			}
			return true;
		}
	}
}
