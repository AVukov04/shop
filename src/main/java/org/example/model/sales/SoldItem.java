package org.example.model.sales;

import java.io.Serializable;

public class SoldItem implements Serializable {
    private String name;
    private int quantity;
    private double unitPrice;

    public SoldItem(String name, int quantity, double unitPrice) {
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public double getTotalPrice() {
        return quantity * unitPrice;
    }

    public String toString() {
        return name + " x" + quantity + " @ " + unitPrice + " = " + getTotalPrice();
    }
}

