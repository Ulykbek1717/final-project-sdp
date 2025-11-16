package com.ecommerce.model;

import com.ecommerce.cart.CartItem;
import java.util.List;
import java.util.UUID;

public class Order {
    private String orderId;
    private String customerEmail;
    private List<CartItem> items;
    private double totalAmount;
    private String paymentMethod;
    
    public Order(String customerEmail, List<CartItem> items, double totalAmount, String paymentMethod) {
        this.orderId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.customerEmail = customerEmail;
        this.items = items;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public List<CartItem> getItems() {
        return items;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
}

