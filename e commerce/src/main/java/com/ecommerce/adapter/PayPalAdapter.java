package com.ecommerce.adapter;

//Adapter for PayPal payment system
public class PayPalAdapter implements PaymentAdapter {
    private PayPalService payPalService;
    
    public PayPalAdapter() {
        this.payPalService = new PayPalService();
    }
    
    @Override
    public boolean processPayment(double amount, String paymentMethod) {
        // Adapt PayPal's specific interface to our common interface
        return payPalService.sendPayment(amount);
    }
    
    @Override
    public String getPaymentProvider() {
        return "PayPal";
    }
    
    // Simulated PayPal service with different interface
    private static class PayPalService {
        public boolean sendPayment(double amount) {
            System.out.println("Processing PayPal payment: $" + amount);
            return true; // Simulated success
        }
    }
}

