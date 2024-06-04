package beans;

import java.time.LocalTime;
import java.util.Collection;

public class Factory {
	private String id;
	private String name;
	private Collection<Chocolate> chocolates;
	private LocalTime openTime;
	private LocalTime closeTime;
	private boolean isOpen;
	private Location location;
	private String logo;
	private double rating;
	
	public Factory() {
	}
	public Factory(String id, String name, Collection<Chocolate> chocolates, LocalTime openTime, LocalTime closeTime,
			boolean isOpen, Location location, String logo, double rating) {
		super();
		this.id = id;
		this.name = name;
		this.chocolates = chocolates;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.isOpen = isOpen;
		this.location = location;
		this.logo = logo;
		this.rating = rating;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Collection<Chocolate> getChocolates() {
		return chocolates;
	}
	public void setChocolates(Collection<Chocolate> chocolates) {
		this.chocolates = chocolates;
	}
	public LocalTime getOpenTime() {
		return openTime;
	}
	public void setOpenTime(LocalTime openTime) {
		this.openTime = openTime;
	}
	public LocalTime getCloseTime() {
		return closeTime;
	}
	public void setCloseTime(LocalTime closeTime) {
		this.closeTime = closeTime;
	}
	public boolean isOpen() {
		return isOpen;
	}
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	
	
}
