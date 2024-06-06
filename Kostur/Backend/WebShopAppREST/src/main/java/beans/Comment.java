package beans;

public class Comment {
	private String id;
	private String customerId;
    private String factoryId;
    private String text;
    private int rating;
    
    public Comment(String id, String customerId, String factoryId, String text, int rating) {
    	this.id = id;
        this.customerId = customerId;
        this.factoryId = factoryId;
        this.text = text;
        this.rating = rating;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(String factoryId) {
        this.factoryId = factoryId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}