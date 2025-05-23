package org.example.model.product;
import java.time.LocalDate;

public interface Sellable {
    double calculateSellingPrice(LocalDate currentDate, int expiryThresholdDays, double discountPercent);
}
