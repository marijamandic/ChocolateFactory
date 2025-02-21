package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

	public ChocolateDAO() {
		this.factoryDAO = new FactoryDAO();
	}

	public ChocolateDAO(String contextPath) {
		this.contextPath = contextPath;
		this.factoryDAO = new FactoryDAO(contextPath);
		loadChocolates(contextPath);
	}

	public ChocolateDAO(String contextPath, FactoryDAO factoryDAO) {
		this.contextPath = contextPath;
		this.factoryDAO = (factoryDAO != null) ? factoryDAO : new FactoryDAO(contextPath);
		loadChocolates(contextPath);
	}

	public Chocolate findChocolateById(String id) {
		Chocolate chocolate = chocolates.get(id);
		if (chocolate == null) {
			System.out.println("Chocolate with ID " + id + " not found in the chocolates map.");
		}
		return chocolate;
	}

	public Collection<Chocolate> findAll() {
		return chocolates.values().stream().collect(Collectors.toList());
	}

	public Collection<Chocolate> findByFactory(String id) {
		return chocolates.values().stream().filter(chocolate -> (chocolate.getFactory().getId().equals(id)))
				.collect(Collectors.toList());
	}

	private void loadChocolates(String contextPath) {
		String filePath = contextPath + "chocolates.csv";
		File file = new File(filePath);

		if (!file.exists()) {
			System.err.println("ERROR: chocolates.csv file not found at " + filePath);
			return;
		} else {
			System.out.println("Loading chocolates from: " + filePath);
		}

		try (BufferedReader in = new BufferedReader(new FileReader(file))) {
			in.readLine(); // presakaknje zaglavlja

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

				Kind kind = Kind.valueOf(parts[5].trim());
				int weight = Integer.parseInt(parts[6].trim());
				String description = parts[7].trim();
				String image = parts[8].trim();
				boolean inStock = Boolean.parseBoolean(parts[9].trim());
				int quantity = Integer.parseInt(parts[10].trim());

				if (factory != null) {
					chocolates.put(id, new Chocolate(id, name, price, type, factory, kind, weight, description, image,
							inStock, quantity));
				} else {
					System.err.println("Factory with ID " + factoryId + " not found for chocolate " + id);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Chocolate addChocolate(Chocolate chocolate) {
		if (chocolates.containsKey(chocolate.getId())) {
			System.out.println("Chocolate with ID " + chocolate.getId() + " already exists.");
			return null;
		}

		chocolate.setId(generateNewId());
		chocolates.put(chocolate.getId(), chocolate);
		System.out.println("Chocolate " + chocolate.getName() + " added successfully.");

		String filepath = contextPath + "chocolates.csv";
		System.out.println("Writing to file: " + filepath);

		try (PrintWriter writer = new PrintWriter(new FileWriter(filepath, true))) {
			writer.println(newChocolateToFileFormat(chocolate));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error while writing chocolate to file.");
		}
		return chocolate;
	}

	public String generateNewId() {
		int maxId = 0;

		String filePath = contextPath + "chocolates.csv";
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

	public boolean deleteChocolate(String chocolateId) {
		String filePath = contextPath + "chocolates.csv";
		
		if(!chocolates.containsKey(chocolateId)) {
			return false;
		}
		
		chocolates.remove(chocolateId);
		
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))){
			writer.write("id,name,price,type,factoryId,kind,weight,description,image,inStock,quantity");
			writer.newLine();
			for(Chocolate chocolate : chocolates.values()) {
				writer.write(chocolateToFileFormat(chocolate));
				writer.newLine();
			}
		} catch(IOException e) {
			e.printStackTrace();
			System.out.println("Error while deleting chocolate from file.");
			return false;
		}
		return true;
	}
	
	public boolean updateChocolate(Chocolate updatedChocolate) {
		String filePath = contextPath + "chocolates.csv";

		if(!chocolates.containsKey(updatedChocolate.getId())) {
			return false;
		}
		
		chocolates.put(updatedChocolate.getId(), updatedChocolate);
		
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))){
			writer.write("id,name,price,type,factoryId,kind,weight,description,image,inStock,quantity");
			writer.newLine();
			for(Chocolate chocolate : chocolates.values()) {
				writer.write(chocolateToFileFormat(chocolate));
				writer.newLine();
			}
		} catch(IOException e) {
			e.printStackTrace();
			System.out.println("Error while updating chocolate from file.");
			return false;
		}
		return true;
	}

	public Collection<Chocolate> getChocolateByFactory(String factoryId) {
		return chocolates.values().stream().filter(chocolate -> chocolate.getFactory().getId().equals(factoryId))
				.collect(Collectors.toList());
	}

	private String newChocolateToFileFormat(Chocolate chocolate) {
		return chocolate.getId() + "," +
				chocolate.getName() + "," + 
				chocolate.getPrice() + "," + 
				chocolate.getType()+ "," + 
				chocolate.getFactory().getId() + "," + 
				chocolate.getKind() + "," + 
				chocolate.getWeight() + "," + 
				chocolate.getDescription() + "," + 
				chocolate.getImage() + "," + 
				"false" + "," + 
				"0";
	}
	
	private String chocolateToFileFormat(Chocolate chocolate) {
		return chocolate.getId() + "," +
				chocolate.getName() + "," + 
				chocolate.getPrice() + "," + 
				chocolate.getType()+ "," + 
				chocolate.getFactory().getId() + "," + 
				chocolate.getKind() + "," + 
				chocolate.getWeight() + "," + 
				chocolate.getDescription() + "," + 
				chocolate.getImage() + "," + 
				chocolate.isInStock() + "," + 
				chocolate.getQuantity();
	}

}