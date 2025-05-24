package org.example.model.shop;

import org.example.model.product.Product;
import org.example.model.sales.Receipt;
import org.example.model.sales.SoldItem;
import org.example.model.staff.Cashier;

import java.io.IOException;
import java.util.*;

public class Shop {
    private List<Product> products;
    private List<Cashier> cashiers;
    private List<Receipt> receipts;

    public Shop() {
        this.products = new ArrayList<>();
        this.cashiers = new ArrayList<>();
        this.receipts = new ArrayList<>();
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public void addCashier(Cashier cashier) {
        cashiers.add(cashier);
    }

    public Product findProductById(String id) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Cashier findCashierById(String id) {
        return cashiers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Receipt sellProducts(Map<String, Integer> productQuantities, String cashierId,
                                int expiryThresholdDays, double discountPercent, String saveFolder) throws IOException {
        Cashier cashier = findCashierById(cashierId);
        if (cashier == null) throw new IllegalArgumentException("Cashier not found");

        Receipt receipt = new Receipt(cashier);

        for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
            String productId = entry.getKey();
            int quantity = entry.getValue();

            Product product = findProductById(productId);
            if (product == null) throw new IllegalArgumentException("Product not found: " + productId);

            if (product.getQuantity() < quantity) {
                throw new IllegalArgumentException("Not enough quantity for product: " + product.getName());
            }

            double unitPrice = product.calculateSellingPrice(java.time.LocalDate.now(), expiryThresholdDays, discountPercent);
            receipt.addItem(new SoldItem(product.getName(), quantity, unitPrice));
            product.setQuantity(product.getQuantity() - quantity);
        }

        receipts.add(receipt);
        receipt.saveToFile(saveFolder);
        return receipt;
    }

    public double calculateTotalIncome() {
        return receipts.stream().mapToDouble(Receipt::getTotal).sum();
    }

    public double calculateTotalSalaries() {
        return cashiers.stream().mapToDouble(Cashier::getMonthlySalary).sum();
    }

    public double calculateDeliveryCosts() {
        return products.stream()
                .mapToDouble(p -> p.getDeliveryPrice() * p.getQuantity())
                .sum();
    }

    public double calculateProfit() {
        return calculateTotalIncome() - calculateTotalSalaries() - calculateDeliveryCosts();
    }

    public int getTotalReceipts() {
        return receipts.size();
    }
}

