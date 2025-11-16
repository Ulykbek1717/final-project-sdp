package com.ecommerce.observer;

import com.ecommerce.model.Product;
import java.util.ArrayList;
import java.util.List;

//Subject class that notifies observers about product changes

public class ProductSubject {
    private List<ProductObserver> observers = new ArrayList<>();
    
    public void addObserver(ProductObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(ProductObserver observer) {
        observers.remove(observer);
    }
    
    public void notifyPriceChange(Product product, double oldPrice, double newPrice) {
        for (ProductObserver observer : observers) {
            observer.onPriceChanged(product, oldPrice, newPrice);
        }
    }
    
    public void notifyStockChange(Product product, int oldStock, int newStock) {
        for (ProductObserver observer : observers) {
            observer.onStockChanged(product, oldStock, newStock);
        }
    }
}

