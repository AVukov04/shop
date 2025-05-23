package org.example.model.product;

import org.example.model.pricing.PriceCalculator;
import java.time.LocalDate;

public class NonFoodProduct extends Product {
    public NonFoodProduct(String id, String name, double deliveryPrice, LocalDate expirationDate, int quantity, PriceCalculator priceCalculator) {
        super(id, name, deliveryPrice, expirationDate, quantity, priceCalculator);
    }
}



