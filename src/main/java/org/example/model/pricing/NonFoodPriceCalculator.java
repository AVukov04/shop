package org.example.model.pricing;

import org.example.model.product.Product;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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

        double basePrice = product.getDeliveryPrice() * (1 + markupPercent / 100);

        long daysToExpiry = ChronoUnit.DAYS.between(currentDate, product.getExpirationDate());

        if (daysToExpiry < expiryThresholdDays) {
            basePrice *= (1 - discountPercent / 100);
        }

        return basePrice;
    }
}


