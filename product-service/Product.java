public class Product {
    private String id;
    private String name;
    private double price;
    private int quantity;
    
    public Product(String id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String toJsonString() {
        return String.format("{\"id\":\"%s\", \"name\":\"%s\", \"price\":%.2f, \"quantity\":%d}", 
                           id, name, price, quantity);
    }
    
    public String toString() {
        return toJsonString();
    }
}