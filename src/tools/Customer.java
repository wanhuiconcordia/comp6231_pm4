package tools;
import java.io.Serializable;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
/**
 * @author comp6231.team5
 *
 */
public class Customer implements Serializable{
	public int customerReferenceNumber;
	public String name;
	public String password;
	public String street1;
	public String street2;
	public String city;
	public String state;
	public String zip;
	public String country;

	/**
	 * Default constructor
	 */
	public Customer(){
		customerReferenceNumber = -1;
		name = new String();
		password = new String();
		street1 = new String();
		street2 = new String();
		city = new String();
		state = new String();
		zip = new String();
		country = new String();
	}
	
	/**
	 * Constructor
	 * @param int customerReferenceNumber
	 * @param String name
	 * @param String password
	 * @param String street1
	 * @param String street2
	 * @param String city
	 * @param String state
	 * @param String zip
	 * @param String country
	 */
	public Customer(int customerReferenceNumber,
			String name,
			String password,
			String street1,
			String street2,
			String city,
			String state,
			String zip,
			String country){
		this.customerReferenceNumber = customerReferenceNumber;
		this.name = name;
		this.password = password;
		this.street1 = street1;
		this.street2 = street2;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.country = country;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "CustomerReferenceNumber:" + customerReferenceNumber 
				+ ", Name:" + name
				+ ", Password:" + password
				+ ", Street1:" + street1
				+ ", Street2:" + street2
				+ ", City:" + city
				+ ", State:" + state
				+ ", Zip:" + zip
				+ ", Country:" + country;
	}
	
	
	/**
	 * store the customer to an xml element
	 * @return element format of the customer
	 */
	public Element toXmlElement() {
		DefaultElement customerElem = new DefaultElement("customer");
		
		Element subElem = customerElem.addElement("customerReferenceNumber");
		subElem.setText(String.valueOf(customerReferenceNumber));
		
		subElem = customerElem.addElement("name");
		subElem.setText(name);
		
		subElem = customerElem.addElement("password");
		subElem.setText(password);
		
		subElem = customerElem.addElement("street1");
		subElem.setText(street1);
		
		subElem = customerElem.addElement("street2");
		subElem.setText(street2);
		
		subElem = customerElem.addElement("city");
		subElem.setText(city);
		
		subElem = customerElem.addElement("state");
		subElem.setText(state);
		
		subElem = customerElem.addElement("zip");
		subElem.setText(zip);
		
		subElem = customerElem.addElement("country");
		subElem.setText(country);
		return customerElem;
	}

	public boolean isSame(Customer otherCustomer) {
		return customerReferenceNumber == otherCustomer.customerReferenceNumber
				&& name.equals(otherCustomer.name)
				&& password.equals(otherCustomer.password)
				&& street1.equals(otherCustomer.street1)
				&& street2.equals(otherCustomer.street2)
				&& city.equals(otherCustomer.city)
				&& state.equals(otherCustomer.state)
				&& zip.equals(otherCustomer.zip)
				&& country.equals(otherCustomer.country);
	}
}
