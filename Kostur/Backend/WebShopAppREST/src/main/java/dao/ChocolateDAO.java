package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import beans.Chocolate;
import beans.Factory;
import beans.enums.ChocolateType;
import beans.enums.Kind;

public class ChocolateDAO {
	private Map<String, Chocolate> chocolates = new HashMap<>();
	private FactoryDAO factoryDAO;
	private String contextPath;
	
	public ChocolateDAO() {}
	
	public ChocolateDAO(String contextPath) {
		this.contextPath = contextPath;
		factoryDAO = new FactoryDAO(contextPath);
		loadChocolates(contextPath);
	}
	public ChocolateDAO(String contextPath, FactoryDAO factoryDAO) {
		this.contextPath = contextPath;
	    this.factoryDAO = (factoryDAO != null) ? factoryDAO : new FactoryDAO(contextPath);  // Provera null vrednosti
	    loadChocolates(contextPath);
	}
	
	public Chocolate findChocolateById(String id) {
	    Chocolate chocolate = chocolates.get(id);
	    return (chocolate != null && !chocolate.getIsDeleted()) ? chocolate : null;
	}


	public Collection<Chocolate> findAll() {
	    return chocolates.values().stream()
	                      .filter(chocolate -> !chocolate.getIsDeleted())
	                      .collect(Collectors.toList());
	}

	
    private void loadChocolates(String contextPath) {
        try (BufferedReader in = new BufferedReader(new FileReader(new File(contextPath + "/chocolates.csv")))) {
            in.readLine();	// presakaknje zaglavlja

            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                String[] parts = line.split(",");
                if (parts.length < 11) {
                    System.err.println("Invalid chocolate entry: " + line);
                    continue;
                }

                String id = parts[0].trim();
                String name = parts[1].trim();
                double price = Double.parseDouble(parts[2].trim());
                ChocolateType type = ChocolateType.valueOf(parts[3].trim());
                String factoryId = parts[4].trim();
                Factory factory = factoryDAO.findFactoryById(factoryId);

                if (factory == null) {
                    System.err.println("Factory not found for ID: " + factoryId);
                    continue;
                }

                Kind kind = Kind.valueOf(parts[5].trim());
                int weight = Integer.parseInt(parts[6].trim());
                String description = parts[7].trim();
                String image = parts[8].trim();
                boolean inStock = Boolean.parseBoolean(parts[9].trim());
                int quantity = Integer.parseInt(parts[10].trim());
                boolean isDeleted = Boolean.parseBoolean(parts[11].trim());

                chocolates.put(id, new Chocolate(id, name, price, type, factory, kind, weight, description, image, inStock, quantity, isDeleted));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

	
    public Chocolate addChocolate(Chocolate chocolate) {
        Set<Integer> existingIds = chocolates.keySet().stream()
                .map(Integer::parseInt)  // pretvori ID-jeve u brojeve
                .filter(id -> !chocolates.get(id).getIsDeleted())  // filtriraj obrisane čokolade
                .collect(Collectors.toSet());

        int maxId = 0;
        while (existingIds.contains(maxId)) {
            maxId++;
        }

        chocolate.setId(String.valueOf(maxId));
        chocolates.put(chocolate.getId(), chocolate);
        System.out.println("Added chocolate: " + chocolate.getId() + ", " + chocolate.getName());
        factoryDAO.addChocolateToFactory(chocolate.getFactory().getId(), chocolate);
        saveAllChocolates();
        return chocolate;
    }


	public Chocolate updateChocolate(String id, Chocolate chocolate) {
		Chocolate c = findChocolateById(id);
		if (c == null) {
			return null;
		}
		if (chocolate.getName() != null) c.setName(chocolate.getName());
	    if (chocolate.getPrice() != 0) c.setPrice(chocolate.getPrice());
	    if (chocolate.getType() != null) c.setType(chocolate.getType());
	    if (chocolate.getFactory() != null) c.setFactory(chocolate.getFactory());
	    if (chocolate.getKind() != null) c.setKind(chocolate.getKind());
	    if (chocolate.getWeight() != 0) c.setWeight(chocolate.getWeight());
	    if (chocolate.getDescription() != null) c.setDescription(chocolate.getDescription());
	    if (chocolate.getImage() != null) c.setImage(chocolate.getImage());
	    c.setInStock(chocolate.isInStock());
	    if (chocolate.getQuantity() >= 0) c.setQuantity(chocolate.getQuantity());


        saveAllChocolates();
		return c;
	}

	public Chocolate deleteChocolate(String id) {
		Chocolate chocolate = chocolates.get(id);
        if (chocolate != null) {
            chocolate.setIsDeleted(true);
            saveAllChocolates(); 
        }
        return chocolate;
	}
/*	
	private void saveAllChocolates() {
        BufferedWriter out = null;
        try {
            File file = new File(contextPath + "/chocolates.txt");
            out = new BufferedWriter(new FileWriter(file));

            for (Chocolate chocolate : chocolates.values()) {
                out.write(chocolateToFileFormat(chocolate));
                out.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
*/
	
	public Collection<Chocolate> getChocolateByFactory(String factoryId) {
		return chocolates.values().stream()
	            .filter(chocolate -> chocolate.getFactory().getId().equals(factoryId))
	            .collect(Collectors.toList());
	}
	
	private void saveAllChocolates() {
		try (BufferedWriter out = new BufferedWriter(new FileWriter(contextPath + "/chocolates.csv", true))) {
			File file = new File(contextPath + "/chocolates.csv");
			
			if (file.length() == 0) {
	            out.write("id,name,price,type,factoryId,kind,weight,description,image,inStock,quantity,isDeleted");
	            out.newLine();
	        }
			
			for (Chocolate chocolate : chocolates.values()) {
	            if (!chocolate.getIsDeleted()) {
	                out.write(chocolateToFileFormat(chocolate));
	                out.newLine();
	                System.out.println("Saved chocolate: " + chocolate.getId() + ", " + chocolate.getName());
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	
    private String chocolateToFileFormat(Chocolate chocolate) {
        return chocolate.getId() + "," +
               chocolate.getName() + "," +
               chocolate.getPrice() + "," +
               chocolate.getType() + "," +
               chocolate.getFactory().getId() + "," +
               chocolate.getKind() + "," +
               chocolate.getWeight() + "," +
               chocolate.getDescription() + "," +
               chocolate.getImage() + "," +
               chocolate.isInStock() + "," +
               chocolate.getQuantity() + "," +
               chocolate.getIsDeleted();
    }

    
}