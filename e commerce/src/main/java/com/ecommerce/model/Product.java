package com.ecommerce.model;

import java.util.HashMap;
import java.util.Map;

// Product class using Builder pattern for customizable product configurations
public class Product {
    private String id;
    private String name;
    private double basePrice;
    private int stock;
    private Map<String, String> configurations;
    
    private Product(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.basePrice = builder.basePrice;
        this.stock = builder.stock;
        this.configurations = new HashMap<>(builder.configurations);
    }
    
    public static class Builder {
        private String id;
        private String name;
        private double basePrice;
        private int stock;
        private Map<String, String> configurations = new HashMap<>();
        
        public Builder(String id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public Builder basePrice(double price) {
            this.basePrice = price;
            return this;
        }
        
        public Builder stock(int stock) {
            this.stock = stock;
            return this;
        }
        
        public Builder addConfiguration(String key, String value) {
            this.configurations.put(key, value);
            return this;
        }
        
        public Builder configurations(Map<String, String> configs) {
            this.configurations.putAll(configs);
            return this;
        }
        
        public Product build() {
            return new Product(this);
        }
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getBasePrice() { return basePrice; }
    public int getStock() { return stock; }
    public Map<String, String> getConfigurations() { return new HashMap<>(configurations); }
    
    // Setters for Observer pattern
    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }
    
    public void setStock(int stock) {
        this.stock = stock;
    }
}

