package org.example.model.pricing;

import org.example.model.product.Product;
import java.time.LocalDate;

public interface PriceCalculator {
    double calculatePrice(Product product, LocalDate currentDate, int expiryThresholdDays, double discountPercent);
}



