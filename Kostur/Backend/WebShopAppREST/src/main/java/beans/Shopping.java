package beans;

import java.time.LocalDateTime;
import java.util.List;

import beans.enums.Status;

public class Shopping {
	private String id;
	private List<Chocolate> boughtChocolates;
	private Factory factory;
	private LocalDateTime shoppingDateTime;
	private double price;
	private Status status;
	private User customer;
	
	public Shopping() {}

	public Shopping(String id, List<Chocolate> boughtChocolates, Factory factory, LocalDateTime shoppingDateTime,
			double price, Status status, User customer) {
		super();
		this.id = id;
		this.boughtChocolates = boughtChocolates;
		this.factory = factory;
		this.shoppingDateTime = shoppingDateTime;
		this.price = price;
		this.status = status;
		this.customer = customer;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Chocolate> getBoughtChocolates() {
		return boughtChocolates;
	}

	public void setBoughtChocolates(List<Chocolate> boughtChocolates) {
		this.boughtChocolates = boughtChocolates;
	}

	public Factory getFactory() {
		return factory;
	}

	public void setFactory(Factory factory) {
		this.factory = factory;
	}

	public LocalDateTime getShoppingDateTime() {
		return shoppingDateTime;
	}

	public void setShoppingDateTime(LocalDateTime shoppingDateTime) {
		this.shoppingDateTime = shoppingDateTime;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public User getCustomer() {
		return customer;
	}

	public void setCustomer(User customer) {
		this.customer = customer;
	}
	
	
}