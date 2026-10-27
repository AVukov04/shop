package org.example.model.shop;

/** Thrown when an order requests more units than the shop has in stock. */
public class InsufficientStockException extends IllegalArgumentException {
    public InsufficientStockException(String productName, int missingQuantity) {
        super("Not enough quantity for product: " + productName
                + ". Missing quantity: " + missingQuantity);
    }
}
