package beans;

import beans.enums.Kind;
import beans.enums.ChocolateType;

public class Chocolate {
	private String id;
	private String name; 
    private double price; 
    private ChocolateType type; 
    private Factory factory; 
    private Kind kind; 
    private int weight; 
    private String description;
    private String image; 
    private boolean inStock; 
    private int quantity;
    
    public Chocolate() {}
	public Chocolate(String id, String name, double price, ChocolateType type, Factory factory, Kind kind, int weight,
			String description, String image, boolean inStock, int quantity) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
		this.type = type;
		this.factory = factory;
		this.kind = kind;
		this.weight = weight;
		this.description = description;
		this.image = image;
		this.inStock = inStock;
		this.quantity = quantity;
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
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public ChocolateType getType() {
		return type;
	}
	public void setType(ChocolateType type) {
		this.type = type;
	}
	public Factory getFactory() {
		return factory;
	}
	public void setFactory(Factory factory) {
		this.factory = factory;
	}
	public Kind getKind() {
		return kind;
	}
	public void setKind(Kind kind) {
		this.kind = kind;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public boolean isInStock() {
		return inStock;
	}
	public void setInStock(boolean inStock) {
		this.inStock = inStock;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}