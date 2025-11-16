package com.ecommerce.strategy;

//Fixed amount discount strategy
public class FixedAmountDiscountStrategy implements DiscountStrategy {
    private double fixedAmount;
    
    public FixedAmountDiscountStrategy(double fixedAmount) {
        this.fixedAmount = fixedAmount;
    }
    
    @Override
    public double calculateDiscount(double originalPrice, int quantity) {
        return fixedAmount * quantity;
    }
    
    @Override
    public String getDescription() {
        return "$" + fixedAmount + " off per item";
    }
}

