package org.example.model.shop;

import org.example.model.pricing.FoodPriceCalculator;
import org.example.model.product.FoodProduct;
import org.example.model.staff.Cashier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ShopTest {

    private Shop shop;

    @BeforeEach
    public void setUp() {
        shop = new Shop();
        shop.addCashier(new Cashier("C001", "Test Cashier", 1000));

        FoodPriceCalculator calculator = new FoodPriceCalculator(30);
        FoodProduct product = new FoodProduct("P001", "Test Milk", 1.0,
                LocalDate.now().plusDays(5), 10, calculator);
        shop.addProduct(product);
    }

    @Test
    public void testSuccessfulSale() throws IOException {
        Map<String, Integer> order = new HashMap<>();
        order.put("P001", 2);

        File testFolder = new File("test_receipts");
        if (!testFolder.exists()) testFolder.mkdirs();

        var receipt = shop.sellProducts(order, "C001", 2, 10, "test_receipts");

        assertEquals(8, shop.findProductById("P001").getQuantity());
        assertTrue(receipt.getTotal() > 0);
        assertTrue(new File("test_receipts/receipt_" + receipt.getNumber() + ".txt").exists());
    }

    @Test
    public void testSaleWithInvalidProduct() {
        Map<String, Integer> order = new HashMap<>();
        order.put("INVALID_ID", 1);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            shop.sellProducts(order, "C001", 2, 10, "test_receipts");
        });

        assertTrue(exception.getMessage().contains("Product not found"));
    }

    @Test
    public void testSaleWithInsufficientQuantity() {
        Map<String, Integer> order = new HashMap<>();
        order.put("P001", 999);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            shop.sellProducts(order, "C001", 2, 10, "test_receipts");
        });

        assertTrue(exception.getMessage().contains("Not enough quantity"));
    }

    @Test
    public void testLoadReceiptsFromFolder() throws IOException {
        // Arrange
        Cashier cashier = new Cashier("C002", "Petar", 1000);
        shop.addCashier(cashier);

        var receipt = new org.example.model.sales.Receipt(cashier);
        receipt.addItem(new org.example.model.sales.SoldItem("Test Item", 2, 1.5));
        receipt.saveAsSerialized("test_receipts");

        int before = shop.getTotalReceipts();

        shop.loadReceiptsFromFolder("test_receipts");

        int after = shop.getTotalReceipts();
        assertTrue(after > before);
    }
}
