package com.ecommerce.observer;

import com.ecommerce.model.Product;
import java.util.HashMap;
import java.util.Map;

//Inventory Manager that observes product changes and updates inventory

public class InventoryManager implements ProductObserver {
    private Map<String, Integer> inventory = new HashMap<>();
    
    @Override
    public void onPriceChanged(Product product, double oldPrice, double newPrice) {
        System.out.println("Inventory Manager: Price changed for " + product.getName() + 
                         " from $" + oldPrice + " to $" + newPrice);
    }
    
    @Override
    public void onStockChanged(Product product, int oldStock, int newStock) {
        inventory.put(product.getId(), newStock);
        System.out.println("Inventory Manager: Stock updated for " + product.getName() + 
                         " from " + oldStock + " to " + newStock);
    }
    
    public int getStock(String productId) {
        return inventory.getOrDefault(productId, 0);
    }
}

