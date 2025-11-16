package com.ecommerce.strategy;

//Bulk discount strategy - discount applies when quantity threshold is met
public class BulkDiscountStrategy implements DiscountStrategy {
    private int threshold;
    private double discountPercentage;
    
    public BulkDiscountStrategy(int threshold, double discountPercentage) {
        this.threshold = threshold;
        this.discountPercentage = discountPercentage;
    }
    
    @Override
    public double calculateDiscount(double originalPrice, int quantity) {
        if (quantity >= threshold) {
            return originalPrice * quantity * (discountPercentage / 100.0);
        }
        return 0;
    }
    
    @Override
    public String getDescription() {
        return discountPercentage + "% off when buying " + threshold + " or more";
    }
}

