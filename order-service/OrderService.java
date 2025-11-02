import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class OrderService {
    private static Map<String, Order> orders = new HashMap<>();
    private static int orderCounter = 1;
    
    public static void main(String[] args) throws IOException {
    // Use environment port or default
    String port = System.getenv("PORT");
    if (port == null) {
        port = "8082"; // Default for order-service
    }
    
    HttpServer server = HttpServer.create(new InetSocketAddress(Integer.parseInt(port)), 0);
        // Serve HTML page from file
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                try {
                    // Read the HTML file
                    byte[] response = Files.readAllBytes(Paths.get("index.html"));
                    exchange.getResponseHeaders().set("Content-Type", "text/html");
                    exchange.sendResponseHeaders(200, response.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response);
                    os.close();
                    System.out.println("Served beautiful HTML page");
                } catch (IOException e) {
                    // Fallback if HTML file not found
                    String simpleHtml = "<html><body><h1>Order Service</h1><p>HTML file not found</p></body></html>";
                    exchange.getResponseHeaders().set("Content-Type", "text/html");
                    exchange.sendResponseHeaders(200, simpleHtml.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(simpleHtml.getBytes());
                    os.close();
                    System.out.println("HTML file not found, served fallback");
                }
            }
        });
        
        // API: Create order
        server.createContext("/order", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("POST".equals(exchange.getRequestMethod())) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                    String requestData = reader.readLine();
                    
                    String productId = "1";
                    int quantity = 1;
                    if (requestData != null && requestData.contains("=")) {
                        String[] parts = requestData.split("&");
                        productId = parts[0].split("=")[1];
                        quantity = Integer.parseInt(parts[1].split("=")[1]);
                    }
                    
                    System.out.println("POST /order - productId: " + productId + ", quantity: " + quantity);
                    
                    String response;
                    if (checkProductExists(productId)) {
                        String orderId = "ORD" + orderCounter++;
                        Order order = new Order(orderId, productId, quantity);
                        order.setStatus("CONFIRMED");
                        orders.put(orderId, order);
                        response = "SUCCESS: " + order.toJsonString();
                        exchange.sendResponseHeaders(200, response.getBytes().length);
                    } else {
                        response = "ERROR: Product not found";
                        exchange.sendResponseHeaders(400, response.getBytes().length);
                    }
                    
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            }
        });
        
        // API: Get all orders
        server.createContext("/orders", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                System.out.println("GET /orders");
                StringBuilder jsonBuilder = new StringBuilder();
                jsonBuilder.append("[");
                boolean first = true;
                for (Order order : orders.values()) {
                    if (!first) jsonBuilder.append(",");
                    jsonBuilder.append(order.toJsonString());
                    first = false;
                }
                jsonBuilder.append("]");
                
                String response = jsonBuilder.toString();
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });
        
        server.start();
        System.out.println("✅ Order Service running on http://localhost:8082");
    }
    
    private static boolean checkProductExists(String productId) {
        try {
            URL url = new URL("http://product-service:8081/product?id=" + productId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            return (responseCode == 200);
        } catch (Exception e) {
            return false;
        }
    }
}