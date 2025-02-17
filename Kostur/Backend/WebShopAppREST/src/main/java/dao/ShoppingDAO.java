package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.Chocolate;
import beans.Factory;
import beans.Shopping;
import beans.User;
import beans.enums.Status;

public class ShoppingDAO{
	
	private Map<String, Shopping> shoppings = new HashMap<>();
	private FactoryDAO factoryDAO;
	private UserDAO userDAO;
	private ChocolateDAO chocolateDAO;
	private String contextPath;
	
	public ShoppingDAO() {}
	
	public ShoppingDAO(String contextPath) {
		this.contextPath = contextPath;
		this.factoryDAO = new FactoryDAO(contextPath);
		this.userDAO = new UserDAO(contextPath);
		this.chocolateDAO = new ChocolateDAO(contextPath);
		loadShopping(contextPath);
	}
	
	public void loadShopping(String contextPath) {
	    String filePath = contextPath + "shoppings.csv";
	    File file = new File(filePath);
	    
	    if (!file.exists()) {
	        System.out.println("ERROR: shoppings.csv file not found at " + filePath);
	        return;
	    } else {
	        System.out.println("Loading shoppings from: " + filePath);
	    }
	    
	    try (BufferedReader in = new BufferedReader(new FileReader(file))) {
	        in.readLine();  // preskakanje zaglavlja
	        
	        String line;
	        while ((line = in.readLine()) != null) {
	            System.out.println("Reading shopping line: " + line);
	            
	            line = line.trim();
	            if (line.isEmpty() || line.startsWith("#"))
	                continue;
	            
	            String[] parts = line.split(",");
	            if (parts.length < 7) {
	                System.out.println("Invalid shopping entry: " + line);
	                continue;
	            }
	            
	            String id = parts[0].trim();
	            String[] boughtChocolates = parts[1].trim().split(";");
	            String factoryId = parts[2].trim();
	            LocalDateTime shoppingDateTime = LocalDateTime.parse(parts[3].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
	            double price = Double.parseDouble(parts[4].trim());
	            Status status = Status.valueOf(parts[5].trim());
	            String customerId = parts[6].trim();
	            
	            Factory factory = factoryDAO.findFactoryById(factoryId);
	            User customer = userDAO.findById(customerId);
	            
	            if (factory != null && customer != null) {
	                List<Chocolate> chocolatesList = new ArrayList<>();
	                for (String chocolateId : boughtChocolates) {
	                    Chocolate chocolate = chocolateDAO.findChocolateById(chocolateId);
	                    if (chocolate != null) {
	                        chocolatesList.add(chocolate);
	                    }
	                }
	                
	                Shopping shopping = new Shopping(id, chocolatesList, factory, shoppingDateTime, price, status, customer);
	                shoppings.put(id, shopping);
	            } else {
	                System.err.println("Invalid factory or customer for shopping " + id);
	            }
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}

	
	public Shopping findById(String id) {
		Shopping shopping = shoppings.get(id);
		if(shopping == null) {
			System.out.println("Shopping with ID " + id + " not found in the shoppings map.");
		}
		return shopping;
	}
	
	
}