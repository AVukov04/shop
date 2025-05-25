package org.example.model.sales;

import org.example.model.staff.Cashier;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

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
}
