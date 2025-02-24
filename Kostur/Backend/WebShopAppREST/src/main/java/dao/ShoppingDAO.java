package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import beans.Chocolate;
import beans.Factory;
import beans.Shopping;
import beans.ShoppingCart;
import beans.User;
import beans.enums.Role;
import beans.enums.Status;

public class ShoppingDAO{
	
	private Map<String, Shopping> shoppings = new HashMap<>();
	private FactoryDAO factoryDAO;
	private UserDAO userDAO;
	private ChocolateDAO chocolateDAO;
	private ShoppingCartDAO shoppingCartDAO;
	private String contextPath;
	
	public ShoppingDAO() {}
	
	public ShoppingDAO(String contextPath) {
		this.contextPath = contextPath;
		this.factoryDAO = new FactoryDAO(contextPath);
		this.userDAO = new UserDAO(contextPath);
		this.chocolateDAO = new ChocolateDAO(contextPath);
		this.shoppingCartDAO = new ShoppingCartDAO(contextPath);
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
	            if (parts.length < 5) {
	                System.out.println("Invalid shopping entry: " + line);
	                continue;
	            }
	            
	            String id = parts[0].trim();
	            String factoryId = parts[1].trim();
	            LocalDateTime shoppingDateTime = LocalDateTime.parse(parts[2].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
	            Status status = Status.valueOf(parts[3].trim());
	            String shoppingCartId = parts[4].trim();
	            
	            Factory factory = factoryDAO.findFactoryById(factoryId);
	            ShoppingCart shoppingCart = shoppingCartDAO.findById(shoppingCartId);
	            
	            if (factory != null) {	                
	                Shopping shopping = new Shopping(id, factory, shoppingDateTime, status, shoppingCart);
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
	
	public List<Shopping> findAll(){
		return shoppings.values().stream()
				.collect(Collectors.toList());
	}
	
	public String generateNewId() {
	    int maxId = 0;

	    String filePath = contextPath + "shoppingCarts.csv";
	    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            String[] fields = line.split(",");
	            String idString = fields[0].trim();
	            try {
	                int id = Integer.parseInt(idString);
	                if (id > maxId) {
	                    maxId = id;
	                }
	            } catch (NumberFormatException e) {
	                System.out.println("Skipping invalid ID: " + idString);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    int newId = maxId + 1;
	    
	    return Integer.toString(newId);
	}
	
	public Shopping addShopping(Shopping shopping) {
		if(shoppings.containsKey(shopping.getId())) {
			System.out.println("Shopping with ID " + shopping.getId() + " already exists in map.");
			return null;
		}
		
		shopping.setId(generateNewId());
		shoppings.put(shopping.getId(), shopping);
		
		double score = shopping.getShoppingCart().getPrice()/1000 * 133;
		userDAO.addScore(shopping.getShoppingCart().getId(), score);
		System.out.println("Shopping " + shopping.getId() + " added successfully");
		
		String filePath = contextPath + "shoppings.csv";
		System.out.println("Writing to file: " + filePath);
		
		try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
			writer.println(newShoppingToFileFormat(shopping));
	    } catch (IOException e) {
	        e.printStackTrace();
	        System.out.println("Error while writing user to file.");
	    }

	    return shopping;
	}
	
	public boolean removeShopping(Shopping shopping) {
	    if (!shoppings.containsKey(shopping.getId())) {
	        System.out.println("Shopping with ID " + shopping.getId() + " does not exist in map.");
	        return false;
	    }

	    User user = shopping.getShoppingCart().getUser();
	    if (user == null) {
	        System.out.println("User for shopping cart not found.");
	        return false;
	    }

	    double score = shopping.getShoppingCart().getPrice() / 1000 * 133 * 4;
	    userDAO.removeScore(user.getId(), score);

	    shopping.setStatus(Status.valueOf("CANCELED"));
	    shoppings.put(shopping.getId(), shopping);
	    System.out.println("Shopping " + shopping.getId() + " removed successfully.");

	    String filePath = contextPath + "shoppings.csv";
	    System.out.println("Updating file: " + filePath);

	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
	        writer.write("id,factory,shoppingDateTime,status,shoppingCart");
	        writer.newLine();
	        for (Shopping s : shoppings.values()) {
	            writer.write(shoppingToFileFormat(s));
	            writer.newLine();
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        System.out.println("Error while updating shoppings file.");
	        return false;
	    }

	    return true;
	}
	
	public String newShoppingToFileFormat(Shopping shopping) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	    String formattedDate = shopping.getShoppingDateTime().format(formatter);
	    
		return shopping.getId() + "," + 
				shopping.getFactory().getId() + "," + 
				formattedDate + "," +
				"PROCESSING," + 
				shopping.getShoppingCart().getId();
	}
	
	public String shoppingToFileFormat(Shopping shopping) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	    String formattedDate = shopping.getShoppingDateTime().format(formatter);
	    
		return shopping.getId() + "," + 
				shopping.getFactory().getId() + "," + 
				formattedDate + "," +
				shopping.getStatus() + "," + 
				shopping.getShoppingCart().getId();
	}
	
}