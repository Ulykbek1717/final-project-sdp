package com.ecommerce.observer;

import com.ecommerce.model.Product;
import com.ecommerce.cart.ShoppingCart;

//Cart Observer that updates shopping cart when prices change
public class CartObserver implements ProductObserver {
    private ShoppingCart cart;
    
    public CartObserver(ShoppingCart cart) {
        this.cart = cart;
    }
    
    @Override
    public void onPriceChanged(Product product, double oldPrice, double newPrice) {
        if (cart.containsProduct(product.getId())) {
            cart.updateProductPrice(product.getId(), newPrice);
            System.out.println("Cart updated: Price changed for " + product.getName() + 
                             " in cart from $" + oldPrice + " to $" + newPrice);
        }
    }
    
    @Override
    public void onStockChanged(Product product, int oldStock, int newStock) {
        if (newStock == 0 && cart.containsProduct(product.getId())) {
            System.out.println("Warning: " + product.getName() + " is out of stock!");
        }
    }
}

