package org.example.model.product;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public abstract class Product {
    private String id;
    private String name;
    private double deliveryPrice;
    private LocalDate expirationDate;
    private int quantity;

    public Product(String id, String name, double deliveryPrice, LocalDate expirationDate, int quantity) {
        this.id = id;
        this.name = name;
        this.deliveryPrice = deliveryPrice;
        this.expirationDate = expirationDate;
        this.quantity = quantity;
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

    // Абстрактен метод за получаване на надценка в проценти, дефинирана в подкласовете
    protected abstract double getMarkupPercent();

    // Основната логика за изчисляване на продажната цена – централизирана тук
    public double calculateSellingPrice(LocalDate currentDate, int expiryThresholdDays, double discountPercent) {
        if (isExpired(currentDate)) {
            return 0.0;
        }

        double basePrice = deliveryPrice * (1 + getMarkupPercent() / 100);

        long daysToExpiry = ChronoUnit.DAYS.between(currentDate, expirationDate);

        if (daysToExpiry < expiryThresholdDays) {
            basePrice *= (1 - discountPercent / 100);
        }

        return basePrice;
    }

    @Override
    public String toString() {
        return name + " (ID: " + id + ")";
    }
}


