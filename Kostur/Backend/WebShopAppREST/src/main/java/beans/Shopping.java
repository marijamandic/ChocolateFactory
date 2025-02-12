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
}