package org.example.model.shop;

import org.example.model.product.Product;
import org.example.model.shop.InsufficientStockException;
import org.example.model.sales.Receipt;
import org.example.model.sales.SoldItem;
import org.example.model.staff.Cashier;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Shop {
    private List<Product> products;
    private List<Cashier> cashiers;
    private List<Receipt> receipts;
    private double totalDeliveryCosts;

    public Shop() {
        this.products = new ArrayList<>();
        this.cashiers = new ArrayList<>();
        this.receipts = new ArrayList<>();
        this.totalDeliveryCosts = 0.0;
    }

    public void addProduct(Product product) {
        Objects.requireNonNull(product, "Product cannot be null");
        products.add(product);
        totalDeliveryCosts += product.getDeliveryPrice() * product.getQuantity();
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
        if (cashier == null) {
            throw new IllegalArgumentException("Cashier not found: " + cashierId);
        }
        if (productQuantities == null || productQuantities.isEmpty()) {
            throw new IllegalArgumentException("The order must contain at least one product.");
        }
        if (expiryThresholdDays < 0) {
            throw new IllegalArgumentException("Expiry threshold cannot be negative.");
        }
        if (discountPercent < 0 || discountPercent > 100) {
            throw new IllegalArgumentException("Discount percent must be between 0 and 100.");
        }

        // Validate the complete order before changing stock, so a failed order
        // cannot leave the shop with partially reduced quantities.
        Map<Product, Integer> validatedProducts = new LinkedHashMap<>();
        Map<Product, Double> unitPrices = new LinkedHashMap<>();
        java.time.LocalDate today = java.time.LocalDate.now();

        for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
            String productId = entry.getKey();
            Integer requestedQuantity = entry.getValue();

            if (requestedQuantity == null || requestedQuantity <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero for product: " + productId);
            }

            Product product = findProductById(productId);
            if (product == null) {
                throw new IllegalArgumentException("Product not found: " + productId);
            }

            if (product.isExpired(today)) {
                throw new IllegalArgumentException("Product is expired and cannot be sold: " + product.getName());
            }

            if (product.getQuantity() < requestedQuantity) {
                int missingQuantity = requestedQuantity - product.getQuantity();
                throw new InsufficientStockException(product.getName(), missingQuantity);
            }

            double unitPrice = product.calculateSellingPrice(today, expiryThresholdDays, discountPercent);
            validatedProducts.put(product, requestedQuantity);
            unitPrices.put(product, unitPrice);
        }

        Receipt.ensureNextNumberAfterFiles(saveFolder);
        Receipt receipt = new Receipt(cashier);
        for (Map.Entry<Product, Integer> entry : validatedProducts.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            receipt.addItem(new SoldItem(product.getName(), quantity, unitPrices.get(product)));
        }

        // Apply stock changes only after every line in the order has passed validation.
        for (Map.Entry<Product, Integer> entry : validatedProducts.entrySet()) {
            Product product = entry.getKey();
            product.setQuantity(product.getQuantity() - entry.getValue());
        }

        receipts.add(receipt);
        receipt.saveToFile(saveFolder);
        receipt.saveAsSerialized(saveFolder);
        return receipt;
    }

    public double calculateTotalIncome() {
        return receipts.stream().mapToDouble(Receipt::getTotal).sum();
    }

    public double calculateTotalSalaries() {
        return cashiers.stream().mapToDouble(Cashier::getMonthlySalary).sum();
    }

    public double calculateDeliveryCosts() {
        return totalDeliveryCosts;
    }

    public double calculateProfit() {
        return calculateTotalIncome() - calculateTotalSalaries() - calculateDeliveryCosts();
    }

    public int getTotalReceipts() {
        return receipts.size();
    }

    public void loadReceiptsFromFolder(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Folder does not exist: " + folderPath);
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".ser"));
        if (files == null || files.length == 0) {
            System.out.println("No receipt files found in: " + folderPath);
            return;
        }

        int loaded = 0;
        double total = 0;

        for (File file : files) {
            try {
                Receipt r = Receipt.loadFromSerialized(file.getAbsolutePath());
                boolean alreadyLoaded = receipts.stream()
                        .anyMatch(existing -> existing.getNumber() == r.getNumber());
                if (!alreadyLoaded) {
                    receipts.add(r);
                    loaded++;
                    total += r.getTotal();
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Failed to load: " + file.getName() + " → " + e.getMessage());
            }
        }

        System.out.println("New receipts added to memory: " + loaded + ".");
        System.out.printf("Revenue from newly loaded receipts: %.2f%n", total);
    }
}


