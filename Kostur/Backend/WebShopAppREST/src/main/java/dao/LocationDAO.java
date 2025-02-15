package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import beans.Location;

public class LocationDAO {
	private Map<String, Location> locations = new HashMap<>();
	
	public LocationDAO() {}
	
	public LocationDAO(String contextPath) {
		loadLocations(contextPath);
	}
	
	public Location findLocationById(String locationId) {
	    return locations.get(locationId);
	}
	
	private void loadLocations(String contextPath) {
	    try (BufferedReader in = new BufferedReader(new FileReader(new File(contextPath + "/locations.csv")))) {
	        String line;
	        if ((line = in.readLine()) != null) {
	            line = line.trim();
	            if (line.startsWith("#")) {
	                return;
	            }
	        }

	        while ((line = in.readLine()) != null) {
	            line = line.trim();
	            if (line.isEmpty() || line.startsWith("#"))
	                continue;

	            String[] parts = line.split(",");
	            if (parts.length < 4) {
	                System.err.println("Invalid location entry: " + line);
	                continue;
	            }

	            String id = parts[0].trim();
	            double longitude = Double.parseDouble(parts[1].trim());
	            double latitude = Double.parseDouble(parts[2].trim());
	            String address = parts[3].trim();

	            locations.put(id, new Location(id, longitude, latitude, address));
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}


}