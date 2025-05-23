package org.example.model.product;

import java.time.LocalDate;

public class FoodProduct extends Product {
    private double markupPercent;

    public FoodProduct(String id, String name, double deliveryPrice, LocalDate expirationDate, int quantity, double markupPercent) {
        super(id, name, deliveryPrice, expirationDate, quantity);
        this.markupPercent = markupPercent;
    }

    @Override
    protected double getMarkupPercent() {
        return markupPercent;
    }

    public double getMarkupPercentValue() {
        return markupPercent;
    }

    public void setMarkupPercent(double markupPercent) {
        this.markupPercent = markupPercent;
    }
}


