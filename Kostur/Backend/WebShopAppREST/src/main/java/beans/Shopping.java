package beans;

import java.time.LocalDateTime;
import java.util.List;

import beans.enums.Status;

public class Shopping {
	private String id;
	private Factory factory;
	private LocalDateTime shoppingDateTime;
	private Status status;
	private ShoppingCart shoppingCart;
	
	public Shopping() {}

	public Shopping(String id, Factory factory, LocalDateTime shoppingDateTime, Status status, ShoppingCart shoppingCart) {
		super();
		this.id = id;
		this.factory = factory;
		this.shoppingDateTime = shoppingDateTime;
		this.status = status;
		this.shoppingCart = shoppingCart;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public ShoppingCart getShoppingCart() {
		return shoppingCart;
	}

	public void setShoppingCart(ShoppingCart shoppingCart) {
		this.shoppingCart = shoppingCart;
	}
}