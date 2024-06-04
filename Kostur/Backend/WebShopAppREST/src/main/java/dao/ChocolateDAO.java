package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
		this.factoryDAO = factoryDAO;
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
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/chocolates.txt");
			System.out.println("File path loading: " + file.getAbsolutePath());
			in = new BufferedReader(new FileReader(file));
			String line;
			StringTokenizer st;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				while (st.hasMoreTokens()) {
					String id = st.nextToken().trim();
	                String name = st.nextToken().trim();
	                double price = Double.parseDouble(st.nextToken().trim());
	                ChocolateType type = ChocolateType.valueOf(st.nextToken().trim());
	                
	                String factoryId = st.nextToken().trim();
	                Factory factory = factoryDAO.findFactoryById(factoryId);

	                Kind kind = Kind.valueOf(st.nextToken().trim());
	                int weight = Integer.parseInt(st.nextToken().trim());
	                String description = st.nextToken().trim();
	                String image = st.nextToken().trim();
	                boolean inStock = Boolean.parseBoolean(st.nextToken().trim());
	                int quantity = Integer.parseInt(st.nextToken().trim());
	                boolean isDeleted = Boolean.parseBoolean(st.nextToken().trim());
	                
	                chocolates.put(id, new Chocolate(id, name, price, type, factory, kind, weight, description, image, inStock, quantity, isDeleted));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (Exception e) { }
			}
		}
	}
	
	public Chocolate addChocolate(Chocolate chocolate) {
		Integer maxId = -1;
		for (String id : chocolates.keySet()) {
			int idNum = Integer.parseInt(id);
			if (idNum > maxId) {
				maxId = idNum;
			}
		}
		maxId++;
		chocolate.setId(maxId.toString());
		chocolates.put(chocolate.getId(), chocolate);
		factoryDAO.addChocolateToFactory(chocolate.getFactory().getId(), chocolate);
        saveAllChocolates();
		return chocolate;
	}
	
	public Chocolate updateChocolate(String id, Chocolate chocolate) {
		Chocolate c = findChocolateById(id);
		if (c == null) {
			return null;
		} else {
			c.setName(chocolate.getName());
			c.setPrice(chocolate.getPrice());
			c.setType(chocolate.getType());
			c.setFactory(chocolate.getFactory());
			c.setKind(chocolate.getKind());
			c.setWeight(chocolate.getWeight());
			c.setDescription(chocolate.getDescription());
			c.setImage(chocolate.getImage());
			c.setInStock(chocolate.isInStock());
			c.setQuantity(chocolate.getQuantity());
		}
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

    private String chocolateToFileFormat(Chocolate chocolate) {
        return chocolate.getId() + ";" +
               chocolate.getName() + ";" +
               chocolate.getPrice() + ";" +
               chocolate.getType() + ";" +
               chocolate.getFactory().getId() + ";" +
               chocolate.getKind() + ";" +
               chocolate.getWeight() + ";" +
               chocolate.getDescription() + ";" +
               chocolate.getImage() + ";" +
               chocolate.isInStock() + ";" +
               chocolate.getQuantity() + ";" +
               chocolate.getIsDeleted();
    }


}