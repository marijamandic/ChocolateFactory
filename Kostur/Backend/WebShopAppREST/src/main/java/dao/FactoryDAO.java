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
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/factories.txt");
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
				    LocalTime openTime = LocalTime.parse(st.nextToken().trim()); 
				    LocalTime closeTime = LocalTime.parse(st.nextToken().trim());
				    
				    String locationId = st.nextToken().trim();
	                Location location = locationDAO.findLocationById(locationId);

				    String logo = st.nextToken().trim();

				    Collection<Chocolate> chocolates = new ArrayList<>();
				    while (st.hasMoreTokens()) {
	                    String chocolateId = st.nextToken().trim();
	                    chocolates.add(chocolateDAO.findChocolateById(chocolateId));
	                }

	                LocalTime now = LocalTime.now();
	                boolean isOpen = now.isAfter(openTime) && now.isBefore(closeTime);
	                double rating = commentDAO.calculateAverageRating(id);
				    factories.put(id, new Factory(id, name, chocolates, openTime, closeTime, isOpen, location, logo, rating));
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
	
	public Collection<Factory> getFilteredFactories() {
		Collection<Factory> filteredFactories = new ArrayList<>();
	    
	    for (Factory factory : factories.values()) {
	        if (factory.isOpen()) {
	        	filteredFactories.add(factory);
	        }
	    }
	    
	    for (Factory factory : factories.values()) {
	        if (!factory.isOpen()) {
	        	filteredFactories.add(factory);
	        }
	    }
	    
	    return filteredFactories;
	}
	
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
		Collection<Chocolate> chocolates = factory.getChocolates();
		chocolates.add(chocolate);
		factory.setChocolates(chocolates);
        saveAllFactories();
		return factory;
	}
	
	private void saveAllFactories() {
        BufferedWriter out = null;
        try {
            File file = new File(contextPath + "/factories.txt");
            out = new BufferedWriter(new FileWriter(file));

            for (Factory factory : factories.values()) {
                out.write(factoryToFileFormat(factory));
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

	private String factoryToFileFormat(Factory factory) {
	    return factory.getId() + ";" +
	           factory.getName() + ";" +
	           factory.getOpenTime() + ";" +
	           factory.getCloseTime() + ";" +
	           factory.getLocation().getId() + ";" +
	           factory.getLogo() + ";" +
	           chocolatesToFileFormat(factory.getChocolates());
	}
	
	private String chocolatesToFileFormat(Collection<Chocolate> chocolates) {
	    if (chocolates == null || chocolates.isEmpty()) {
	        return "null";
	    }
	    return chocolates.stream()
	                     .map(Chocolate::getId)
	                     .collect(Collectors.joining(";"));
	}
	
	public void setChocolatesToFactory(Collection<Chocolate> chocolates, String factoryId) {
		Collection<Chocolate> choco = new ArrayList();
		for(Chocolate chocolate : chocolates) {
			if(chocolate.getFactory().getId().equals(factoryId)) {
				choco.add(chocolate);
			}
		}
		Factory factory = findFactoryById(factoryId);
		factory.setChocolates(choco);
	}
}