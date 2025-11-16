package com.ecommerce.adapter;

//Adapter for Credit Card payment system

public class CreditCardAdapter implements PaymentAdapter {
    private CreditCardProcessor creditCardProcessor;
    
    public CreditCardAdapter() {
        this.creditCardProcessor = new CreditCardProcessor();
    }
    
    @Override
    public boolean processPayment(double amount, String paymentMethod) {
        // Adapt Credit Card's specific interface to our common interface
        return creditCardProcessor.chargeCard(amount, paymentMethod);
    }
    
    @Override
    public String getPaymentProvider() {
        return "Credit Card";
    }
    
    // Simulated Credit Card processor with different interface
    private static class CreditCardProcessor {
        public boolean chargeCard(double amount, String cardNumber) {
            System.out.println("Processing Credit Card payment: $" + amount + " for card: " + cardNumber);
            return true; // Simulated success
        }
    }
}

