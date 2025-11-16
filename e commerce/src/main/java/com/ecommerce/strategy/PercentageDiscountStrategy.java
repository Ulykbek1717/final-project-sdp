package com.ecommerce.strategy;

//Percentage-based discount strategy
public class PercentageDiscountStrategy implements DiscountStrategy {
    private double percentage;
    
    public PercentageDiscountStrategy(double percentage) {
        this.percentage = percentage;
    }
    
    @Override
    public double calculateDiscount(double originalPrice, int quantity) {
        return originalPrice * quantity * (percentage / 100.0);
    }
    
    @Override
    public String getDescription() {
        return percentage + "% off";
    }
}

