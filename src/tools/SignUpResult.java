package tools;

import java.io.Serializable;

/**
 *  If result is true, the customerReferenceNumber and password are generated properly on server
 * Otherwise failed to register. 
 * @author comp6231.team5
 *
 */
public class SignUpResult implements Serializable{
	public int customerReferenceNumber;
	public boolean result;
	public String message;	//Server will give a message about the sign up status
	
	/**
	 * Default constructor 
	 */
	public SignUpResult(){
		customerReferenceNumber = 0;
		result = false;
		message = new String();
	}
	
	/**
	 * constructor
	 * @param result
	 * @param customerReferenceNumber
	 * @param message
	 */
	public SignUpResult(boolean result, int customerReferenceNumber, String message){
		this.customerReferenceNumber = customerReferenceNumber;
		this.result = result;
		this.message = message;
	}
	
	@Override
	public String toString(){
		return customerReferenceNumber + ", " + result + ", " + message;
	}
	
	public boolean isSame(SignUpResult other) {
		return customerReferenceNumber == other.customerReferenceNumber
				&& result == other.result
				&& message.equals(other.message); 
	}
}
