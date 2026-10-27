package org.example.model.pricing;

import java.time.LocalDate;

public interface Sellable {
    double calculateSellingPrice(LocalDate currentDate, int expiryThresholdDays, double discountPercent);
}

