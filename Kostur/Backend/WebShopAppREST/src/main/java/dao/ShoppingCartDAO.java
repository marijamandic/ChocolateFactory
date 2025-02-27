package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import beans.Chocolate;
import beans.ShoppingCart;
import beans.User;
import beans.enums.Role;

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
	
	public List<ShoppingCart> findAll(){
		return shoppingCarts.values().stream().collect(Collectors.toList());
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
	
	public ShoppingCart addCart(ShoppingCart shoppingCart) {
	    if (shoppingCarts.containsKey(shoppingCart.getId())) {
	        System.out.println("Shopping cart with ID " + shoppingCart.getId() + " already exists.");
	        return null;
	    }
	    
	    shoppingCart.setId(generateNewId());
	    shoppingCart.setPrice(0.0);
	    shoppingCarts.put(shoppingCart.getId(), shoppingCart);
	    System.out.println("Shopping cart " + shoppingCart.getId() + " added successfully.");
	    
	    String filePath = contextPath + "shoppingCarts.csv";
	    System.out.println("Writing to file: " + filePath);

	    try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
	    	writer.println(shoppingCartToFileFormat(shoppingCart));
	    } catch (IOException e) {
	        e.printStackTrace();
	        System.out.println("Error while writing shopping cart to file.");
	    }

	    return shoppingCart;
	}
	
	public ShoppingCart addToCart(String id, String chocolateId) {
	    ShoppingCart shoppingCart = shoppingCarts.get(id);
	    Chocolate chocolate = chocolateDAO.findChocolateById(chocolateId);
	    
	    if(chocolate == null) {
	    	System.out.println("Chocolate with ID: " + chocolateId + " not found.");
	    	return null;
	    }
	    
	    if(shoppingCart == null) {
	    	System.out.println("Chocolate with ID: " + chocolateId + " not found.");
	    	return null;
	    }
	    
	    shoppingCart.getChocolates().add(chocolate);
	    double newPrice = shoppingCart.getChocolates().stream()
                .mapToDouble(Chocolate::getPrice)
                .sum();
		shoppingCart.setPrice(newPrice);
		
	    shoppingCarts.put(id, shoppingCart);
	    writeAllShoppingCartsToFile();
	    
	    return shoppingCart;
	}
	
	public ShoppingCart removeFromCart(String id, String chocolateId) {
	    ShoppingCart shoppingCart = shoppingCarts.get(id);
	    if (shoppingCart == null) {
	        System.out.println("Shopping cart with ID: " + id + " not found.");
	        return null;
	    }

	    Chocolate chocolate = chocolateDAO.findChocolateById(chocolateId);
	    if (chocolate == null) {
	        System.out.println("Chocolate with ID: " + chocolateId + " not found.");
	        return null;
	    }

	    List<Chocolate> chocolates = shoppingCart.getChocolates();
	    for (int i = 0; i < chocolates.size(); i++) {
	        if (chocolates.get(i).getId().equals(chocolate.getId())) {
	            chocolates.remove(i);
	            break;
	        }
	    }

	    double newPrice = chocolates.stream()
	            .mapToDouble(Chocolate::getPrice)
	            .sum();
	    shoppingCart.setPrice(newPrice);

	    shoppingCarts.put(id, shoppingCart);
	    writeAllShoppingCartsToFile();

	    System.out.println("Chocolate with ID: " + chocolate.getId() + " removed successfully.");
	    return shoppingCart;
	}

	
	private String shoppingCartToFileFormat(ShoppingCart shoppingCart) {
		return shoppingCart.getId() + "," + 
				chocolatesToString(shoppingCart.getChocolates()) + "," + 
				shoppingCart.getUser().getId() + "," +
				shoppingCart.getPrice();
	}
	
	private void writeAllShoppingCartsToFile() {
	    String filePath = contextPath + "shoppingCarts.csv";
	    System.out.println("Rewriting file: " + filePath);

	    try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
	    	writer.println("id,chocolates,userId,price");
	        for (ShoppingCart cart : shoppingCarts.values()) {
	            writer.println(shoppingCartToFileFormat(cart));
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        System.out.println("Error while writing all shopping carts to file.");
	    }
	}
	
	private String chocolatesToString(List<Chocolate> chocolates) {
	    return chocolates.stream()
	                     .map(Chocolate::getId)
	                     .collect(Collectors.joining(";"));
	}

}