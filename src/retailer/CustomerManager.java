package retailer;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;

import tools.Customer;
import tools.SignUpResult;

import tools.XmlFileController;

/**
 * @author comp6231.team5
 * Maintain customers
 */
public class CustomerManager {

	private ArrayList<Customer> customers;

	private String fileName;

	/**
	 * Constructor
	 * @param String fileName
	 */
	public CustomerManager(String fileName){
		customers = new ArrayList<Customer>();
		this.fileName = fileName;
		loadCustomers();
	}

	/**
	 * Save customers information to an XML file. 
	 */
	public void saveCustomers()
	{
		XmlFileController xmlFileControler = new XmlFileController(fileName);
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("customers");
		for(Customer customerImpl: customers){
			Element ml = customerImpl.toXmlElement();
			root.add(ml);
		}
		try {
			xmlFileControler.Write(document);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load customers information from an XML file 
	 */
	public void loadCustomers()
	{
		XmlFileController xmlfile = new XmlFileController(fileName);
		Element root = xmlfile.Read();
		if(root != null){
			List<Element> nodes = root.elements("customer");
			for(Element subElem: nodes){
				int customerReferenceNumber = Integer.parseInt(subElem.element("customerReferenceNumber").getText());
				String name = subElem.element("name").getText();
				String password = subElem.element("password").getText(); 
				String street1 = subElem.element("street1").getText();
				String street2 = subElem.element("street2").getText();
				String city = subElem.element("city").getText();
				String state = subElem.element("state").getText();
				String zip = subElem.element("zip").getText();
				String country = subElem.element("country").getText();
				customers.add(new Customer(customerReferenceNumber, name, password, street1, street2, city, state, zip, country));
			}
		}
	}

	/**
	 * Process customer registration
	 * @param name
	 * @param password
	 * @param street1
	 * @param street2
	 * @param city
	 * @param state
	 * @param zip
	 * @param country
	 * @return SignUpResult
	 */
	public synchronized SignUpResult register(String name, String password, String street1, String street2, String city, String state, String zip, String country){
		for(Customer customer: customers){
			if(customer.name.equals(name) && customer.password.equals(password)){
				return new SignUpResult(false, -1, "Failed to sign up! (User name exists, try another name)");
			}
		}
		int customerReferenceNumber = 1000 + customers.size();
		customers.add(new Customer(customerReferenceNumber, name, password, street1, street2, city, state, zip, country));
		saveCustomers();
		return new SignUpResult(true, customerReferenceNumber , "Sign up successfully.");
	}

	/**
	 * Find the customerReferenceNumber from the customers list.
	 * @param customerReferenceNumber
	 * @param password
	 * @return Customer if found otherwise return null
	 */
	public synchronized Customer find(int customerReferenceNumber, String password){
		for(Customer customer: customers){
			if(customer.customerReferenceNumber == customerReferenceNumber
					&& customer.password.equals(password)){
				return customer;
			}
		}
		return null;
	}

	/**
	 * Find the customerReferenceNumber from the customers list.
	 * @param customerReferenceNumber
	 * @return Customer if found otherwise return null
	 */
	public synchronized boolean find(int customerReferenceNumber){
		for(Customer customer: customers){
			if(customer.customerReferenceNumber== customerReferenceNumber){
				return true;
			}
		}
		return false;
	}

	/**
	 * get customer by referenceNumber
	 * @param customerReferenceNumber
	 * @return Customer if matching customerReferenceNumber otherwise return null
	 */
	public synchronized Customer getCustomerByReferenceNumber(int customerReferenceNumber){
		for(Customer customer: customers){
			if(customer.customerReferenceNumber == customerReferenceNumber){
				return customer;
			}
		}
		return null;
	}
}
