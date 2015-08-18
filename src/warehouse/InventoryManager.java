package warehouse;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.tree.DefaultElement;

import tools.*;

/**
 * @author comp6231.team5
 * Maintain inventory.
 */
public class InventoryManager {
	public HashMap<String, Item> inventoryItemMap;
	private String warehouseName;
	/**
	 * Constructor
	 * @param name
	 */
	public InventoryManager(String name){
		
		inventoryItemMap = new HashMap<String, Item>();
		this.warehouseName = name;
	}
	/**
	 * Load items from an XML file to the inventoryItemMap
	 */
	private void loadItems(){
		XmlFileController xmlfile = new XmlFileController(warehouseName + ".xml");
		Element root = xmlfile.Read();
		if(root != null){
			List<Element> nodes = root.elements("item");
			for(Element subElem: nodes){
				String manufacturerName = subElem.element("manufacturerName").getText();
				String productType = subElem.element("productType").getText();
				float unitPrice = Float.parseFloat(subElem.element("unitPrice").getText());
				int quantity = Integer.parseInt(subElem.element("quantity").getText());
				
				inventoryItemMap.put(manufacturerName + productType, new Item(manufacturerName, productType, unitPrice, quantity));
			}
		}
	}
	/**
	 * Convert data to XML Element
	 * @param i
	 * @return Element
	 */
	public Element toXmlElement(Item i){
		DefaultElement customerElem = new DefaultElement("item");
		Element subElem = customerElem.addElement("productID");
		subElem.setText(i.productID);
		subElem = customerElem.addElement("manufacturerName");
		subElem.setText(i.manufacturerName);
		
		subElem = customerElem.addElement("productType");
		subElem.setText(i.productType);
		
		subElem = customerElem.addElement("unitPrice");
		subElem.setText(String.valueOf(i.unitPrice));
		
		subElem = customerElem.addElement("quantity");
		subElem.setText(String.valueOf(i.quantity));
		return customerElem;
		
	}
	/**
	 * Save inventoryItemMap to an XML file 
	 */
	public void saveItems()
	{
		XmlFileController xmlFileControler = new XmlFileController(warehouseName + ".xml");
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("items");
		for(Item item: inventoryItemMap.values()){
			Element itemElem = toXmlElement(item);
			root.add(itemElem);
		}
		try {
			xmlFileControler.Write(document);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}