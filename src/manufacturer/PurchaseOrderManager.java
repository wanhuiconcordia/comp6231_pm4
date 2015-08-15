package manufacturer;

import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;

import tools.Item;
import tools.XmlFileController;

/**
 * @author comp6231.team5
 *
 */

public class PurchaseOrderManager {
	public HashMap<String, Item> itemsMap;
	private String manufacturerName;
	
	/**
	 * @param manufacturerName
	 */
	public PurchaseOrderManager(String manufacturerName){
		itemsMap = new HashMap<String, Item>();
		this.manufacturerName = manufacturerName;
		loadItems();
	}

	/**
	 * load items of current manufacturer from to corresponding xml file
	 * and save them in itemsMap
	 */
	private void loadItems(){
		XmlFileController xmlfile = new XmlFileController(manufacturerName + ".xml");
		Element root = xmlfile.Read();
		if(root != null){
			List<Element> nodes = root.elements("item");
			for(Element subElem: nodes){
				String manufacturerName = subElem.element("manufacturerName").getText();
				String productType = subElem.element("productType").getText();
				float unitPrice = Float.parseFloat(subElem.element("unitPrice").getText());
				int quantity = Integer.parseInt(subElem.element("quantity").getText());
				
				itemsMap.put(productType, new Item(manufacturerName, productType, unitPrice, quantity));
			}
		}
	}
	

	/**
	 * save itemsMap to xml file
	 */
	public void saveItems()
	{
		XmlFileController xmlFileControler = new XmlFileController(manufacturerName + ".xml");
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("items");
		for(Item item: itemsMap.values()){
			Element itemElem = item.toXmlElement();
			root.add(itemElem);
		}
		try {
			xmlFileControler.Write(document);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
