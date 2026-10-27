package org.example.model.pricing;

import org.example.model.product.Product;
import java.time.LocalDate;

public class NonFoodPriceCalculator implements PriceCalculator {

    private final double markupPercent;

    public NonFoodPriceCalculator(double markupPercent) {
        this.markupPercent = markupPercent;
    }

    @Override
    public double calculatePrice(Product product, LocalDate currentDate, int expiryThresholdDays, double discountPercent) {
        if (product.isExpired(currentDate)) {
            return 0.0;
        }

        return product.getDeliveryPrice() * (1 + markupPercent / 100);
    }
}



