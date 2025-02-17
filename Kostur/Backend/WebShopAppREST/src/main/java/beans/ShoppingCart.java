package beans;

import java.util.List;

public class ShoppingCart {
	private String id;
	private List<Chocolate> chocolates;
	private User user;
	private double price;

	public ShoppingCart() {}

	public ShoppingCart(String id, List<Chocolate> chocolates, User user, double price) {
		super();
		this.id = id;
		this.chocolates = chocolates;
		this.user = user;
		this.price = price;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Chocolate> getChocolates() {
		return chocolates;
	}

	public void setChocolates(List<Chocolate> chocolates) {
		this.chocolates = chocolates;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	
}