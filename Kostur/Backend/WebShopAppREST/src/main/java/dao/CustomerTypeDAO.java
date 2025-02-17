package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import beans.CustomerType;
import beans.enums.TypeName;

public class CustomerTypeDAO{
	private Map<String, CustomerType> customerTypes = new HashMap<>();
	private String contextPath;
	
	public CustomerTypeDAO() {}
	
	public CustomerTypeDAO(String contextPath) {
		this.contextPath = contextPath;
		loadCustomerTypes(contextPath);
	}
	
	public CustomerType findById(String id) {
		CustomerType customerType = customerTypes.get(id);
		if(customerType ==  null) {
			System.out.println("CustomerType with ID " + id + " not found in the customerTypes map.");
		}
		return customerType;
	}
	
	public void loadCustomerTypes(String contextPath) {
	    String filePath = contextPath + "customerTypes.csv";
	    File file = new File(filePath);

	    if (!file.exists()) {
	        System.out.println("ERROR: customerTypes.csv file not found at " + filePath);
	        return;
	    } else {
	        System.out.println("Loading customer types from: " + filePath);
	    }

	    try (BufferedReader in = new BufferedReader(new FileReader(file))) {
	        in.readLine(); // Preskakanje zaglavlja

	        String line;
	        while ((line = in.readLine()) != null) {
	            System.out.println("Reading customer type line: " + line);

	            line = line.trim();
	            if (line.isEmpty() || line.startsWith("#"))
	                continue;

	            String[] parts = line.split(",");
	            if (parts.length < 4) {
	                System.out.println("Invalid customer type entry: " + line);
	                continue;
	            }

	            String id = parts[0].trim();
	            TypeName type = TypeName.valueOf(parts[1].trim());
	            double discount = Double.parseDouble(parts[2].trim());
	            int score = Integer.parseInt(parts[3].trim());

	            customerTypes.put(id, new CustomerType(id, type, discount, score));
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}

}