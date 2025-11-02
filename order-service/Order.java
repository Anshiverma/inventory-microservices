public class Order {
    private String orderId;
    private String productId;
    private int quantity;
    private String status;
    
    public Order(String orderId, String productId, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = "PENDING";
    }
    
    public String getOrderId() { return orderId; }
    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String toJsonString() {
        return String.format("{\"orderId\":\"%s\", \"productId\":\"%s\", \"quantity\":%d, \"status\":\"%s\"}", 
                           orderId, productId, quantity, status);
    }
    
    public String toString() {
        return toJsonString();
    }
}