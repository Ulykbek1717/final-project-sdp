package com.ecommerce.facade;

import com.ecommerce.adapter.PaymentAdapter;
import com.ecommerce.adapter.PayPalAdapter;
import com.ecommerce.adapter.CreditCardAdapter;
import com.ecommerce.cart.ShoppingCart;
import com.ecommerce.cart.CartItem;
import com.ecommerce.model.Order;
import com.ecommerce.model.Product;
import com.ecommerce.observer.CartObserver;
import com.ecommerce.observer.InventoryManager;
import com.ecommerce.observer.ProductSubject;
import com.ecommerce.service.EmailService;
import com.ecommerce.strategy.DiscountStrategy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Facade pattern to simplify interactions with the e-commerce system

public class ECommerceFacade {
    private ShoppingCart cart;
    private ProductSubject productSubject;
    private InventoryManager inventoryManager;
    private CartObserver cartObserver;
    private Map<String, Product> products;
    
    public ECommerceFacade() {
        this.cart = new ShoppingCart();
        this.productSubject = new ProductSubject();
        this.inventoryManager = new InventoryManager();
        this.cartObserver = new CartObserver(cart);
        this.products = new HashMap<>();
        
        // Register observers
        productSubject.addObserver(inventoryManager);
        productSubject.addObserver(cartObserver);
    }
    
    public void addProduct(Product product) {
        products.put(product.getId(), product);
        inventoryManager.onStockChanged(product, 0, product.getStock());
    }
    
    public Product getProduct(String id) {
        return products.get(id);
    }
    
    public void addToCart(String productId, int quantity) {
        Product product = products.get(productId);
        if (product != null && product.getStock() >= quantity) {
            cart.addItem(product, quantity);
        }
    }
    
    public void removeFromCart(String productId) {
        cart.removeItem(productId);
    }
    
    public void updateProductPrice(String productId, double newPrice) {
        Product product = products.get(productId);
        if (product != null) {
            double oldPrice = product.getBasePrice();
            product.setBasePrice(newPrice);
            productSubject.notifyPriceChange(product, oldPrice, newPrice);
        }
    }
    
    public void updateProductStock(String productId, int newStock) {
        Product product = products.get(productId);
        if (product != null) {
            int oldStock = product.getStock();
            product.setStock(newStock);
            productSubject.notifyStockChange(product, oldStock, newStock);
        }
    }
    
    public void applyDiscount(DiscountStrategy strategy) {
        cart.setDiscountStrategy(strategy);
    }
    
    public ShoppingCart getCart() {
        return cart;
    }
    
    public Map<String, Product> getProducts() {
        return new HashMap<>(products);
    }
    
    // Checkout and create order
    public Order checkout(String customerEmail, String paymentMethod, String paymentDetails) {
        if (cart.getItems().isEmpty()) {
            return null;
        }
        
        // Process payment using Adapter pattern
        PaymentAdapter paymentAdapter;
        if ("paypal".equalsIgnoreCase(paymentMethod)) {
            paymentAdapter = new PayPalAdapter();
        } else {
            paymentAdapter = new CreditCardAdapter();
        }
        
        double total = cart.calculateTotal();
        boolean paymentSuccess = paymentAdapter.processPayment(total, paymentDetails);
        
        if (!paymentSuccess) {
            return null;
        }
        
        // Create order
        List<CartItem> orderItems = new ArrayList<>(cart.getItems());
        Order order = new Order(customerEmail, orderItems, total, paymentAdapter.getPaymentProvider());
        
        // Send email confirmation
        EmailService emailService = new EmailService();
        boolean emailSent = emailService.sendOrderConfirmation(customerEmail, order);
        
        if (emailSent) {
            System.out.println("Order confirmation email sent to " + customerEmail);
        } else {
            System.out.println("Failed to send email, but order was created");
        }
        
        // Clear cart after successful checkout
        cart.clear();
        
        return order;
    }
}

