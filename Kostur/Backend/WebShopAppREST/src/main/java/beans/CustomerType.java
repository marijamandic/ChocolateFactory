package beans;

import beans.enums.TypeName;

public class CustomerType {
	private String id;
	private TypeName type;
	private double discount;
	private int score;  
	
	public CustomerType() {}

	public CustomerType(String id, TypeName type, double discount, int score) {
		super();
		this.id = id;
		this.type = type;
		this.discount = discount;
		this.score = score;
	}

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public TypeName getType() {
		return type;
	}

	public void setType(TypeName type) {
		this.type = type;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	
}