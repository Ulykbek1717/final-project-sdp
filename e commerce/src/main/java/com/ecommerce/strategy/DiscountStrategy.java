package com.ecommerce.strategy;

//Strategy interface for discount algorithms

public interface DiscountStrategy {
    double calculateDiscount(double originalPrice, int quantity);
    String getDescription();
}

