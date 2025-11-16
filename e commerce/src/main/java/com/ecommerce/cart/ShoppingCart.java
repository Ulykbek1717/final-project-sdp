package com.ecommerce.cart;

import com.ecommerce.model.Product;
import com.ecommerce.strategy.DiscountStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//Shopping Cart that uses Strategy pattern for discounts
public class ShoppingCart {
    private List<CartItem> items = new ArrayList<>();
    private DiscountStrategy discountStrategy;
    
    public void addItem(Product product, int quantity) {
        Optional<CartItem> existingItem = items.stream()
            .filter(item -> item.getProduct().getId().equals(product.getId()))
            .findFirst();
        
        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            items.add(new CartItem(product, quantity));
        }
    }
    
    public void removeItem(String productId) {
        items.removeIf(item -> item.getProduct().getId().equals(productId));
    }
    
    public void updateQuantity(String productId, int quantity) {
        items.stream()
            .filter(item -> item.getProduct().getId().equals(productId))
            .findFirst()
            .ifPresent(item -> item.setQuantity(quantity));
    }
    
    public void updateProductPrice(String productId, double newPrice) {
        items.stream()
            .filter(item -> item.getProduct().getId().equals(productId))
            .findFirst()
            .ifPresent(item -> item.updatePrice(newPrice));
    }
    
    public boolean containsProduct(String productId) {
        return items.stream()
            .anyMatch(item -> item.getProduct().getId().equals(productId));
    }
    
    public void setDiscountStrategy(DiscountStrategy strategy) {
        this.discountStrategy = strategy;
    }
    
    public double calculateTotal() {
        double subtotal = items.stream()
            .mapToDouble(CartItem::getSubtotal)
            .sum();
        
        if (discountStrategy != null) {
            double discount = items.stream()
                .mapToDouble(item -> discountStrategy.calculateDiscount(
                    item.getCurrentPrice(), item.getQuantity()))
                .sum();
            return subtotal - discount;
        }
        
        return subtotal;
    }
    
    public double calculateDiscount() {
        if (discountStrategy == null) return 0;
        
        return items.stream()
            .mapToDouble(item -> discountStrategy.calculateDiscount(
                item.getCurrentPrice(), item.getQuantity()))
            .sum();
    }
    
    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }
    
    public void clear() {
        items.clear();
    }
}

