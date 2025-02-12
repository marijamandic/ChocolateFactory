package beans;

import java.util.Date;
import java.util.List;

import beans.enums.Gender;
import beans.enums.Role;

public class User {
	private String id;
	private String username;
	private String password;
	private String name;
	private String surname;
	private Gender gender;
	private Date birthday;
	private Role role;
	private CustomerType customerType;
	private List<Shopping> allShoppings;
	private List<Chocolate> shoppingCart;
	private double score;
	private Factory factory;
}