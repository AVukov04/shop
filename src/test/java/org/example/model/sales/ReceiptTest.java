package org.example.model.sales;

import org.example.model.staff.Cashier;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ReceiptTest {

    @Test
    public void testReceiptSerialization() throws IOException, ClassNotFoundException {
        Cashier cashier = new Cashier("C004", "Maria", 1100);
        Receipt receipt = new Receipt(cashier);
        receipt.addItem(new SoldItem("Item1", 2, 5.0));

        String folder = "test_serialization";
        receipt.saveAsSerialized(folder);

        String path = folder + "/receipt_" + receipt.getNumber() + ".ser";
        Receipt loaded = Receipt.loadFromSerialized(path);

        assertEquals(receipt.getTotal(), loaded.getTotal(), 0.001);
        assertEquals(receipt.toString(), loaded.toString());
    }

    @Test
    public void testReceiptTxtFileCreated() throws IOException {
        Cashier cashier = new Cashier("C005", "Anna", 1000);
        Receipt receipt = new Receipt(cashier);
        receipt.addItem(new SoldItem("Apple", 3, 1.2));

        String folder = "test_receipts";
        receipt.saveToFile(folder);

        File file = new File(folder + "/receipt_" + receipt.getNumber() + ".txt");
        assertTrue(file.exists(), "Receipt TXT file was not created.");
    }
    @Test
    public void testReceiptTextFileCanBeRead() throws IOException {
        Cashier cashier = new Cashier("C006", "Ivan", 1200);
        Receipt receipt = new Receipt(cashier);
        receipt.addItem(new SoldItem("Bread", 2, 1.5));

        String folder = "test_receipt_read";
        receipt.saveToFile(folder);
        String path = folder + "/receipt_" + receipt.getNumber() + ".txt";

        String content = Receipt.readFromFile(path);

        assertTrue(content.contains("Receipt #" + receipt.getNumber()));
        assertTrue(content.contains("Ivan"));
        assertTrue(content.contains("Bread"));
        assertTrue(content.contains("Total: 3.00"));
    }

    @Test
    public void testReceiptNumberSkipsNumbersAlreadyPresentInFolder() throws IOException {
        Path folder = Files.createTempDirectory("receipt-number-test");
        Files.createFile(folder.resolve("receipt_700.txt"));

        Receipt.ensureNextNumberAfterFiles(folder.toString());
        Receipt receipt = new Receipt(new Cashier("C007", "Test Cashier", 1000));

        assertTrue(receipt.getNumber() > 700);
    }

}
