package com.ecommerce.observer;

import com.ecommerce.model.Product;

//Observer interface for product updates

public interface ProductObserver {
    void onPriceChanged(Product product, double oldPrice, double newPrice);
    void onStockChanged(Product product, int oldStock, int newStock);
}

