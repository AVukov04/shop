package org.example.model.product;

import org.example.model.pricing.PriceCalculator;
import org.example.model.pricing.Sellable;
import java.time.LocalDate;

public abstract class Product implements Sellable {
    private String id;
    private String name;
    private double deliveryPrice;
    private LocalDate expirationDate;
    private int quantity;

    protected PriceCalculator priceCalculator;

    public Product(String id, String name, double deliveryPrice, LocalDate expirationDate, int quantity, PriceCalculator priceCalculator) {
        this.id = id;
        this.name = name;
        this.deliveryPrice = deliveryPrice;
        this.expirationDate = expirationDate;
        this.quantity = quantity;
        this.priceCalculator = priceCalculator;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getDeliveryPrice() { return deliveryPrice; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isExpired(LocalDate currentDate) {
        return currentDate.isAfter(expirationDate);
    }

    @Override
    public double calculateSellingPrice(LocalDate currentDate, int expiryThresholdDays, double discountPercent) {
        return priceCalculator.calculatePrice(this, currentDate, expiryThresholdDays, discountPercent);
    }

    @Override
    public String toString() {
        return name + " (ID: " + id + ")";
    }
}


