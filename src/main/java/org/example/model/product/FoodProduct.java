package org.example.model.product;

import org.example.model.pricing.PriceCalculator;
import java.time.LocalDate;

public class FoodProduct extends Product {
    public FoodProduct(String id, String name, double deliveryPrice, LocalDate expirationDate, int quantity, PriceCalculator priceCalculator) {
        super(id, name, deliveryPrice, expirationDate, quantity, priceCalculator);
    }
}




