package com.ecommerce.factory;

import com.ecommerce.model.Product;
import java.util.Map;

//Factory Method pattern for creating products
public class ProductFactory {
    public static Product createProduct(String type, String id, String name, double price, int stock, Map<String, String> configs) {
        Product.Builder builder = new Product.Builder(id, name)
            .basePrice(price)
            .stock(stock);
        
        if (configs != null) {
            builder.configurations(configs);
        }
        
        // Add default configurations based on product type
        switch (type.toLowerCase()) {
            case "electronics":
                builder.addConfiguration("warranty", "1 year");
                builder.addConfiguration("category", "Electronics");
                break;
            case "clothing":
                builder.addConfiguration("size", "M");
                builder.addConfiguration("category", "Clothing");
                break;
            case "book":
                builder.addConfiguration("format", "Paperback");
                builder.addConfiguration("category", "Books");
                break;
            default:
                builder.addConfiguration("category", "General");
        }
        
        return builder.build();
    }
}

