import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ProductService {
    private static Map<String, Product> products = new HashMap<>();
    
    public static void main(String[] args) throws IOException {
        // Add sample products
        products.put("1", new Product("1", "Laptop", 999.99, 10));
        products.put("2", new Product("2", "Mouse", 25.50, 50));
        products.put("3", new Product("3", "Keyboard", 45.00, 30));
        
        String port = System.getenv("PORT");
    if (port == null) {
        port = "8081"; // Default for product-service
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
                    String simpleHtml = "<html><body><h1>Product Service</h1><p>HTML file not found</p></body></html>";
                    exchange.getResponseHeaders().set("Content-Type", "text/html");
                    exchange.sendResponseHeaders(200, simpleHtml.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(simpleHtml.getBytes());
                    os.close();
                    System.out.println("HTML file not found, served fallback");
                }
            }
        });
        
        // API: Get all products
        server.createContext("/products", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                System.out.println("GET /products");
                StringBuilder jsonBuilder = new StringBuilder();
                jsonBuilder.append("[");
                boolean first = true;
                for (Product product : products.values()) {
                    if (!first) jsonBuilder.append(",");
                    jsonBuilder.append(product.toJsonString());
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
        
        // API: Get specific product
        server.createContext("/product", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String query = exchange.getRequestURI().getQuery();
                String productId = query != null && query.contains("=") ? query.split("=")[1] : "";
                System.out.println("GET /product?id=" + productId);
                
                Product product = products.get(productId);
                String response = (product != null) ? product.toJsonString() : "{\"error\":\"Product not found\"}";
                
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });
        
        server.start();
        System.out.println("✅ Product Service running on http://localhost:8081");
    }
}