package org.example.model.shop;

import org.example.model.pricing.FoodPriceCalculator;
import org.example.model.product.FoodProduct;
import org.example.model.staff.Cashier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

        InsufficientStockException exception = assertThrows(InsufficientStockException.class, () -> {
            shop.sellProducts(order, "C001", 2, 10, "test_receipts");
        });

        assertTrue(exception.getMessage().contains("Not enough quantity"));
    }

    @Test
    public void testSaleWithZeroOrNegativeQuantityIsRejected() {
        for (int quantity : new int[]{0, -1}) {
            Map<String, Integer> order = new HashMap<>();
            order.put("P001", quantity);
            assertThrows(IllegalArgumentException.class,
                    () -> shop.sellProducts(order, "C001", 2, 10, "test_receipts"));
        }
        assertEquals(10, shop.findProductById("P001").getQuantity());
    }

    @Test
    public void testExpiredProductCannotBeSold() {
        shop.addProduct(new FoodProduct("P002", "Expired Yogurt", 1.0,
                LocalDate.now().minusDays(1), 5, new FoodPriceCalculator(30)));
        Map<String, Integer> order = new HashMap<>();
        order.put("P002", 1);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> shop.sellProducts(order, "C001", 2, 10, "test_receipts"));

        assertTrue(exception.getMessage().contains("expired"));
        assertEquals(5, shop.findProductById("P002").getQuantity());
    }

    @Test
    public void testFailedOrderDoesNotPartiallyReduceStock() {
        shop.addProduct(new FoodProduct("P002", "Second Milk", 1.0,
                LocalDate.now().plusDays(5), 1, new FoodPriceCalculator(30)));
        Map<String, Integer> order = new HashMap<>();
        order.put("P001", 2);
        order.put("P002", 2);

        assertThrows(IllegalArgumentException.class,
                () -> shop.sellProducts(order, "C001", 2, 10, "test_receipts"));

        assertEquals(10, shop.findProductById("P001").getQuantity());
        assertEquals(1, shop.findProductById("P002").getQuantity());
        assertEquals(0, shop.getTotalReceipts());
    }

    @Test
    public void testInsufficientStockExceptionReportsMissingQuantity() {
        Map<String, Integer> order = new HashMap<>();
        order.put("P001", 14);

        InsufficientStockException exception = assertThrows(InsufficientStockException.class,
                () -> shop.sellProducts(order, "C001", 2, 10, "test_receipts"));

        assertTrue(exception.getMessage().contains("Test Milk"));
        assertTrue(exception.getMessage().contains("Missing quantity: 4"));
        assertEquals(10, shop.findProductById("P001").getQuantity());
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
    @Test
    public void testLoadingReceiptFolderTwiceDoesNotDuplicateReceipts() throws IOException {
        Path folder = Files.createTempDirectory("receipt-idempotency-test");

        Cashier cashier = new Cashier("C009", "Idempotency Cashier", 1000);
        var receipt = new org.example.model.sales.Receipt(cashier);
        receipt.addItem(new org.example.model.sales.SoldItem("Test Item", 1, 2.0));
        receipt.saveAsSerialized(folder.toString());

        Shop freshShop = new Shop();
        freshShop.loadReceiptsFromFolder(folder.toString());
        int firstLoadCount = freshShop.getTotalReceipts();
        freshShop.loadReceiptsFromFolder(folder.toString());

        assertEquals(1, firstLoadCount);
        assertEquals(firstLoadCount, freshShop.getTotalReceipts());
    }

}
