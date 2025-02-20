package beans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
	private double score;
	private Factory factory;
	
	public User() {}

	// glavni konstruktor sa svim parametrima
    public User(String id, String username, String password, String name, String surname, Gender gender, Date birthday,
                Role role, CustomerType customerType,
                double score, Factory factory) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.gender = gender;
        this.birthday = birthday;
        this.role = role;
        this.customerType = customerType;
        this.score = score;
        this.factory = factory;
    }

    // konstruktor za obicne korisnike (bez kupovine, korpe i fabrike)
    public User(String id, String username, String password, String name, String surname, Gender gender, Date birthday,
                Role role) {
        this(id, username, password, name, surname, gender, birthday, role, null, 0.0, null);
    }

    // konstruktor za kupce
    public User(String id, String username, String password, String name, String surname, Gender gender, Date birthday,
                Role role, CustomerType customerType, double score) {
        this(id, username, password, name, surname, gender, birthday, role, customerType, score, null);
    }

    // konstruktor za menadzere (imaju fabriku)
    public User(String id, String username, String password, String name, String surname, Gender gender, Date birthday,
                Role role, Factory factory) {
        this(id, username, password, name, surname, gender, birthday, role, null, 0.0, factory);
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Date getBirthday() {
		return birthday;
	}

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public void setBirthday(String birthday) {
		try {
            this.birthday = dateFormat.parse(birthday);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid date format. Expected: yyyy-MM-dd");
        }
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public CustomerType getCustomerType() {
		return customerType;
	}

	public void setCustomerType(CustomerType customerType) {
		this.customerType = customerType;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public Factory getFactory() {
		return factory;
	}

	public void setFactory(Factory factory) {
		this.factory = factory;
	}
	
	
}