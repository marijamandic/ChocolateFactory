package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import beans.Chocolate;
import beans.Factory;
import beans.Location;


public class FactoryDAO {
	private Map<String, Factory> factories = new HashMap<>();
	private LocationDAO locationDAO;
	private ChocolateDAO chocolateDAO;
	private CommentDAO commentDAO;
	private String contextPath;
	
	public FactoryDAO() {}
	
	public FactoryDAO(String contextPath) {
		this.contextPath = contextPath;
		locationDAO = new LocationDAO(contextPath);
		chocolateDAO = new ChocolateDAO(contextPath, this);
		commentDAO = new CommentDAO(contextPath);
		loadFactories(contextPath);
	}
	
	private void loadFactories(String contextPath) {
	    File file = new File(contextPath + "/factories.csv");

	    try (BufferedReader in = new BufferedReader(new FileReader(file))) {
	        in.readLine();	//preskakanje zaglavlja
	    	String line;
	        while ((line = in.readLine()) != null) {
	            line = line.trim();
	            if (line.isEmpty() || line.startsWith("#")) 
	                continue;

	            String[] parts = line.split(",");
	            if (parts.length < 6) {
	                System.err.println("Invalid factory entry: " + line);
	                continue;
	            }

	            String id = parts[0].trim();
	            String name = parts[1].trim();
	            LocalTime openTime = LocalTime.parse(parts[2].trim());
	            LocalTime closeTime = LocalTime.parse(parts[3].trim());
	            String locationId = parts[4].trim();
	            Location location = locationDAO.findLocationById(locationId);

	            if (location == null) {
	                System.err.println("Location not found for ID: " + locationId);
	                continue;
	            }

	            String logo = parts[5].trim();
	            
	            String[] chocolateIds = parts.length > 6 ? parts[6].split(";") : new String[0];
	            Collection<Chocolate> chocolates = new ArrayList<>();

	            for (String chocolateId : chocolateIds) {
	            	chocolateId = chocolateId.trim();
	                Chocolate chocolate = chocolateDAO.findChocolateById(chocolateId);
	                if (chocolate != null) {
	                    chocolates.add(chocolate);
	                } else {
	                    System.err.println("Chocolate not found for ID: " + chocolateId);
	                }
	            }

	            LocalTime now = LocalTime.now();
	            boolean isOpen = now.isAfter(openTime) && now.isBefore(closeTime);
	            double rating = commentDAO.calculateAverageRating(id);

	            factories.put(id, new Factory(id, name, chocolates, openTime, closeTime, isOpen, location, logo, rating));
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}

	
	/*
	 * public Collection<Factory> getFilteredFactories() { Collection<Factory>
	 * filteredFactories = new ArrayList<>();
	 * 
	 * for (Factory factory : factories.values()) { if (factory.isOpen()) {
	 * filteredFactories.add(factory); } }
	 * 
	 * for (Factory factory : factories.values()) { if (!factory.isOpen()) {
	 * filteredFactories.add(factory); } }
	 * 
	 * return filteredFactories; }
	 */	
	public Collection<Factory> getOpenFactories() {
	    return factories.values().stream()
	            .filter(Factory::isOpen)
	            .collect(Collectors.toList());
	}

	public Collection<Factory> findAll() {
		return factories.values();
	}
	
	public Factory findFactoryById(String id) {
		return factories.get(id);
	}
	
	public Factory addChocolateToFactory(String factoryId, Chocolate chocolate) {
		Factory factory = findFactoryById(factoryId);
	    if (factory == null) {
	        System.err.println("Factory not found for ID: " + factoryId);
	        return null;
	    }
	    factory.getChocolates().add(chocolate);
	    saveAllFactories();
	    return factory;
	}
	
	private void saveAllFactories() {
	    File file = new File(contextPath + "/factories.csv");
	    try (BufferedWriter out = new BufferedWriter(new FileWriter(file, true))) { 
	        if (file.length() == 0) {
	            out.write("id,name,openTime,closeTime,locationId,logo,chocolatesId");
	            out.newLine();
	        }

	        for (Factory factory : factories.values()) {
	            out.write(factoryToFileFormat(factory));
	            out.newLine();
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}



	private String factoryToFileFormat(Factory factory) {
	    return factory.getId() + "," +
	           factory.getName() + "," +
	           factory.getOpenTime() + "," +
	           factory.getCloseTime() + "," +
	           factory.getLocation().getId() + "," +
	           factory.getLogo() + "," +
	           chocolatesToFileFormat(factory.getChocolates());
	}
	
	private String chocolatesToFileFormat(Collection<Chocolate> chocolates) {
		return chocolates.isEmpty() ? "" : chocolates.stream()
		        .map(Chocolate::getId)
		        .collect(Collectors.joining(";"));
	}
	
	/*
	 * public void setChocolatesToFactory(Collection<Chocolate> chocolates, String
	 * factoryId) { Collection<Chocolate> choco = new ArrayList(); for(Chocolate
	 * chocolate : chocolates) {
	 * if(chocolate.getFactory().getId().equals(factoryId)) { choco.add(chocolate);
	 * } } Factory factory = findFactoryById(factoryId);
	 * factory.setChocolates(choco); }
	 */
}