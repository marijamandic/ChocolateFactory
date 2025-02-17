package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.Chocolate;
import beans.ShoppingCart;
import beans.User;

public class ShoppingCartDAO{
	private Map<String, ShoppingCart> shoppingCarts = new HashMap<>();
	private ChocolateDAO chocolateDAO;
	private UserDAO userDAO;
	private String contextPath;

	public ShoppingCartDAO() {}
	
	public ShoppingCartDAO(String contextPath) {
		this.contextPath = contextPath;
		this.userDAO = new UserDAO(contextPath);
		this.chocolateDAO = new ChocolateDAO(contextPath);
		loadShoppingCart(contextPath);
	}
	
	public void loadShoppingCart(String contextPath) {
	    String filePath = contextPath + "shoppingCarts.csv";
	    File file = new File(filePath);

	    if (!file.exists()) {
	        System.out.println("ERROR: shoppingCarts.csv file not found at " + filePath);
	        return;
	    } else {
	        System.out.println("Loading shopping carts from: " + filePath);
	    }

	    try (BufferedReader in = new BufferedReader(new FileReader(file))) {
	        in.readLine(); // Preskakanje zaglavlja

	        String line;
	        while ((line = in.readLine()) != null) {
	            System.out.println("Reading shopping cart line: " + line);

	            line = line.trim();
	            if (line.isEmpty() || line.startsWith("#"))
	                continue;

	            String[] parts = line.split(",");
	            if (parts.length < 4) {
	                System.err.println("Invalid shopping cart entry: " + line);
	                continue;
	            }

	            String id = parts[0].trim();
	            String[] chocolateIds = parts[1].trim().split(";");
	            String userId = parts[2].trim();
	            double price = Double.parseDouble(parts[3].trim());

	            User user = userDAO.findById(userId);
	            if (user == null) {
	                System.err.println("User with ID " + userId + " not found for shopping cart " + id);
	                continue;
	            }

	            List<Chocolate> chocolates = new ArrayList<>();
	            for (String chocolateId : chocolateIds) {
	                Chocolate chocolate = chocolateDAO.findChocolateById(chocolateId);
	                if (chocolate != null) {
	                    chocolates.add(chocolate);
	                } else {
	                    System.err.println("Chocolate with ID " + chocolateId + " not found.");
	                }
	            }

	            ShoppingCart cart = new ShoppingCart(id, chocolates, user, price);
	            shoppingCarts.put(id, cart);
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}
	
	public ShoppingCart findById(String id) {
		ShoppingCart shoppingCart = shoppingCarts.get(id);
		if (shoppingCart == null) {
	        System.out.println("Shopping cart with ID " + id + " not found in the shoppingCarts map.");
	    }
		return shoppingCart;
	}

}