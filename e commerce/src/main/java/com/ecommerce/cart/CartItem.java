package com.ecommerce.cart;

import com.ecommerce.model.Product;

public class CartItem {
    private Product product;
    private int quantity;
    private double currentPrice;
    
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.currentPrice = product.getBasePrice();
    }
    
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getCurrentPrice() { return currentPrice; }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public void updatePrice(double newPrice) {
        this.currentPrice = newPrice;
    }
    
    public double getSubtotal() {
        return currentPrice * quantity;
    }
}

