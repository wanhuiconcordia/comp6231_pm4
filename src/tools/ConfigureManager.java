package tools;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * @author comp6231.team5
 *
 */
public class ConfigureManager {
	private static ConfigureManager instance = null;
	private HashMap<String, String> data;
	
	
	/**
	 * Constructor
	 */
	protected ConfigureManager(){
		data = new HashMap<String, String>();
		loadFile("./settings/settings.conf");
	}
	
	/**
	 * get an instance of class ConfigureManager
	 * @return an instance of ConfitureManager
	 */
	public static ConfigureManager getInstance(){
		if(instance == null){
			instance = new ConfigureManager();
		}
		return instance;
	}
	
	/**
	 * load the file with the name of fileName and store the contents into data
	 * @param fileName
	 * @return true if load file successfully ,false if not 
	 */
	public boolean loadFile(String fileName){
		boolean bRet = false;
		try {
			InputStream inputStream = new FileInputStream(fileName);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
			BufferedReader bufferReader = new BufferedReader(inputStreamReader);
			String line;
			String variableName;
			String value;
			while ((line = bufferReader.readLine()) != null) {
				if(line.startsWith("#")){	//comments line
					continue;
				}else if(line.length() == 0){
					continue;
				}else{
					String []fields = line.split("=");
					
					if(fields.length < 2){
						System.out.println("This line does not contain '='!");
					}
					else {
						variableName = fields[0].trim();
						value = fields[1];
						if(fields.length > 2){
							for(int i = 2; i < fields.length; i++)
							{
								value = value + "=" + fields[i];
							}
						}
						value = value.trim();
						data.put(variableName, value);
					}			
				}
			}
			bufferReader.close();
			inputStreamReader.close();
			inputStream.close();
			bRet = true;
		} catch (FileNotFoundException e) {
			System.out.println(fileName + " cannot be found!");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bRet;
	}

	/**
	 * get the String value to which the specific key value is matched in data
	 * @param variableName
	 * @param defaultValue
	 * @return a string value
	 */
	public String getString(String variableName, String defaultValue){
		String value = data.get(variableName);
		if(value == null){
			return defaultValue;
		}
		else{
			return value;
		}
			
	}
	
	/**
	 * get the String value to which the specific key value is matched in data
	 * @param variableName
	 * @param defaultValue
	 * @return a string value
	 * @throws Exception 
	 */
	public String getString(String variableName) throws Exception{
		String value = data.get(variableName);
		if(value == null){
			throw new Exception(variableName + " cannot be found in the config file!");
		}else{
			return value;
		}
			
	}
	
	/**
	 * get the boolean value to which the specific key value is matched in data
	 * @param variableName
	 * @param defaultValue
	 * @return a boolean value
	 */
	public boolean getBool(String variableName, Boolean defaultValue){
		String value = data.get(variableName);
		if(value == null){
			return defaultValue;
		}else{
			return value.equalsIgnoreCase("true");
		}
	}
	
	/**
	 * get the integer value to which the specific key value is matched in data
	 * @param variableName
	 * @param defaultValue
	 * @return an integer value
	 */
	public int getInt(String variableName, int defaultValue){
		String value = data.get(variableName);
		if(value == null){
			return defaultValue;
		}
		else{
			try{
				return Integer.parseInt(value);
			}catch(NumberFormatException e){
				return defaultValue;
			}			
		}
	}
	
	/**
	 * get the integer value to which the specific key value is matched in data
	 * @param variableName
	 * @param defaultValue
	 * @return an integer value
	 * @throws Exception 
	 */
	public int getInt(String variableName) throws Exception{
		String value = data.get(variableName);
		if(value == null){
			throw new Exception(variableName + " cannot be found in the config file!");
		}else{
			return Integer.parseInt(value);
		}			
	}
	
	/**
	 * out put the contents in data to the screen
	 */
	public void display(){
		for (Entry<String, String> entry : data.entrySet()) {
		    String key = entry.getKey();
		    Object value = entry.getValue();
		    System.out.println(key + ":\t" + value);
		}
	}
}
