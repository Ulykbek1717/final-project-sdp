package com.ecommerce.adapter;

//Adapter pattern: Adapts different payment systems to a common interface

public interface PaymentAdapter {
    boolean processPayment(double amount, String paymentMethod);
    String getPaymentProvider();
}

