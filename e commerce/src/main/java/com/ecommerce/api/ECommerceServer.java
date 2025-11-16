package com.ecommerce.api;

import com.ecommerce.facade.ECommerceFacade;
import com.ecommerce.factory.ProductFactory;
import com.ecommerce.model.Product;
import com.ecommerce.strategy.*;
import com.ecommerce.cart.CartItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


//  REST API Server using Facade pattern
public class ECommerceServer {
    private ECommerceFacade facade;
    private Gson gson;
    private HttpServer server;
    
    public ECommerceServer() {
        this.facade = new ECommerceFacade();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        initializeProducts();
    }
    
    private void initializeProducts() {
        // Initialize with sample products
        Map<String, String> config1 = new HashMap<>();
        config1.put("color", "Black");
        config1.put("memory", "256GB");
        Product p1 = ProductFactory.createProduct("electronics", "P001", "Smartphone", 599.99, 50, config1);
        facade.addProduct(p1);
        
        Map<String, String> config2 = new HashMap<>();
        config2.put("color", "Blue");
        config2.put("size", "L");
        Product p2 = ProductFactory.createProduct("clothing", "P002", "T-Shirt", 29.99, 100, config2);
        facade.addProduct(p2);
        
        Product p3 = ProductFactory.createProduct("book", "P003", "Java Design Patterns", 49.99, 30, null);
        facade.addProduct(p3);
        
        Product p4 = ProductFactory.createProduct("electronics", "P004", "Laptop", 1299.99, 25, null);
        facade.addProduct(p4);
    }
    
    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // CORS headers helper
        server.createContext("/api/products", exchange -> {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                setCorsHeaders(exchange);
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            setCorsHeaders(exchange);
            handleProducts(exchange);
        });
        
        server.createContext("/api/cart", exchange -> {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                setCorsHeaders(exchange);
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            setCorsHeaders(exchange);
            handleCart(exchange);
        });
        
        server.createContext("/api/discount", exchange -> {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                setCorsHeaders(exchange);
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            setCorsHeaders(exchange);
            handleDiscount(exchange);
        });
        
        server.createContext("/api/update", exchange -> {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                setCorsHeaders(exchange);
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            setCorsHeaders(exchange);
            handleUpdate(exchange);
        });
        
        server.createContext("/api/checkout", exchange -> {
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                setCorsHeaders(exchange);
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            setCorsHeaders(exchange);
            handleCheckout(exchange);
        });
        
        // Serve static files
        server.createContext("/", exchange -> {
            setCorsHeaders(exchange);
            serveStaticFile(exchange);
        });
        
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + port);
    }
    
    private void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }
    
    private void handleProducts(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            Map<String, Product> products = facade.getProducts();
            Map<String, Object> response = new HashMap<>();
            response.put("products", products.values().stream().map(p -> {
                Map<String, Object> prod = new HashMap<>();
                prod.put("id", p.getId());
                prod.put("name", p.getName());
                prod.put("basePrice", p.getBasePrice());
                prod.put("stock", p.getStock());
                prod.put("configurations", p.getConfigurations());
                return prod;
            }).collect(Collectors.toList()));
            
            sendResponse(exchange, 200, gson.toJson(response));
        } else {
            sendResponse(exchange, 405, "Method not allowed");
        }
    }
    
    private void handleCart(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        
        if ("GET".equals(method)) {
            Map<String, Object> response = new HashMap<>();
            response.put("items", facade.getCart().getItems().stream().map(item -> {
                Map<String, Object> cartItem = new HashMap<>();
                cartItem.put("productId", item.getProduct().getId());
                cartItem.put("productName", item.getProduct().getName());
                cartItem.put("quantity", item.getQuantity());
                cartItem.put("price", item.getCurrentPrice());
                cartItem.put("subtotal", item.getSubtotal());
                return cartItem;
            }).collect(Collectors.toList()));
            response.put("subtotal", facade.getCart().getItems().stream()
                .mapToDouble(CartItem::getSubtotal).sum());
            response.put("discount", facade.getCart().calculateDiscount());
            response.put("total", facade.getCart().calculateTotal());
            
            sendResponse(exchange, 200, gson.toJson(response));
        } else if ("POST".equals(method)) {
            String body = getRequestBody(exchange);
            Map<String, Object> request = gson.fromJson(body, Map.class);
            String productId = (String) request.get("productId");
            int quantity = ((Double) request.get("quantity")).intValue();
            
            facade.addToCart(productId, quantity);
            sendResponse(exchange, 200, gson.toJson(Map.of("success", true)));
        } else if ("DELETE".equals(method)) {
            String query = exchange.getRequestURI().getQuery();
            String productId = query != null ? query.split("=")[1] : null;
            
            if (productId != null) {
                facade.removeFromCart(productId);
                sendResponse(exchange, 200, gson.toJson(Map.of("success", true)));
            } else {
                sendResponse(exchange, 400, gson.toJson(Map.of("error", "Product ID required")));
            }
        } else if ("PUT".equals(method)) {
            String body = getRequestBody(exchange);
            Map<String, Object> request = gson.fromJson(body, Map.class);
            String productId = (String) request.get("productId");
            int quantity = ((Double) request.get("quantity")).intValue();
            
            facade.getCart().updateQuantity(productId, quantity);
            sendResponse(exchange, 200, gson.toJson(Map.of("success", true)));
        } else {
            sendResponse(exchange, 405, "Method not allowed");
        }
    }
    
    private void handleDiscount(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            String body = getRequestBody(exchange);
            Map<String, Object> request = gson.fromJson(body, Map.class);
            String type = (String) request.get("type");
            
            DiscountStrategy strategy = null;
            switch (type) {
                case "percentage":
                    double percentage = ((Double) request.get("value"));
                    strategy = new PercentageDiscountStrategy(percentage);
                    break;
                case "fixed":
                    double fixed = ((Double) request.get("value"));
                    strategy = new FixedAmountDiscountStrategy(fixed);
                    break;
                case "bulk":
                    int threshold = ((Double) request.get("threshold")).intValue();
                    double discount = ((Double) request.get("value"));
                    strategy = new BulkDiscountStrategy(threshold, discount);
                    break;
            }
            
            if (strategy != null) {
                facade.applyDiscount(strategy);
                sendResponse(exchange, 200, gson.toJson(Map.of("success", true, "description", strategy.getDescription())));
            } else {
                sendResponse(exchange, 400, gson.toJson(Map.of("error", "Invalid discount type")));
            }
        } else {
            sendResponse(exchange, 405, "Method not allowed");
        }
    }
    
    private void handleUpdate(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            String body = getRequestBody(exchange);
            Map<String, Object> request = gson.fromJson(body, Map.class);
            String productId = (String) request.get("productId");
            String updateType = (String) request.get("type");
            
            if ("price".equals(updateType)) {
                double newPrice = ((Double) request.get("value"));
                facade.updateProductPrice(productId, newPrice);
            } else if ("stock".equals(updateType)) {
                int newStock = ((Double) request.get("value")).intValue();
                facade.updateProductStock(productId, newStock);
            }
            
            sendResponse(exchange, 200, gson.toJson(Map.of("success", true)));
        } else {
            sendResponse(exchange, 405, "Method not allowed");
        }
    }
    
    private void handleCheckout(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            String body = getRequestBody(exchange);
            Map<String, Object> request = gson.fromJson(body, Map.class);
            String customerEmail = (String) request.get("email");
            String paymentMethod = (String) request.get("paymentMethod");
            String paymentDetails = (String) request.get("paymentDetails");
            
            if (customerEmail == null || customerEmail.trim().isEmpty()) {
                sendResponse(exchange, 400, gson.toJson(Map.of("error", "Email is required")));
                return;
            }
            
            if (paymentMethod == null) {
                paymentMethod = "creditcard";
            }
            
            if (paymentDetails == null) {
                paymentDetails = "payment_details";
            }
            
            com.ecommerce.model.Order order = facade.checkout(customerEmail, paymentMethod, paymentDetails);
            
            if (order != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("orderId", order.getOrderId());
                response.put("message", "Order confirmed! Check your email for confirmation.");
                sendResponse(exchange, 200, gson.toJson(response));
            } else {
                sendResponse(exchange, 400, gson.toJson(Map.of("error", "Checkout failed. Cart may be empty or payment failed.")));
            }
        } else {
            sendResponse(exchange, 405, "Method not allowed");
        }
    }
    
    private void serveStaticFile(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/")) {
            path = "/index.html";
        }
        
        // Remove leading slash for resource path
        String resourcePath = "web" + path;
        
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (is == null) {
                sendResponse(exchange, 404, "File not found: " + path);
                return;
            }
            
            String contentType = "text/html";
            if (path.endsWith(".css")) contentType = "text/css";
            else if (path.endsWith(".js")) contentType = "application/javascript";
            else if (path.endsWith(".json")) contentType = "application/json";
            else if (path.endsWith(".png")) contentType = "image/png";
            else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) contentType = "image/jpeg";
            
            exchange.getResponseHeaders().set("Content-Type", contentType);
            
            // Read all bytes first to get content length
            byte[] buffer = new byte[8192];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            is.close();
            byte[] content = baos.toByteArray();
            
            exchange.sendResponseHeaders(200, content.length);
            OutputStream os = exchange.getResponseBody();
            os.write(content);
            os.close();
        } catch (Exception e) {
            sendResponse(exchange, 500, "Error serving file: " + e.getMessage());
        }
    }
    
    private String getRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
    }
    
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();
    }
    
    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
    
    public static void main(String[] args) throws IOException {
        ECommerceServer server = new ECommerceServer();
        server.start(8080);
    }
}

