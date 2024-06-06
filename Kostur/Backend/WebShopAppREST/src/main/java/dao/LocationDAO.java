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
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/locations.txt");
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
				    double longitude = Double.parseDouble(st.nextToken().trim());
				    double latitude = Double.parseDouble(st.nextToken().trim());
				    String address = st.nextToken().trim();
				    
				    locations.put(id, new Location(id, longitude, latitude, address));
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

}