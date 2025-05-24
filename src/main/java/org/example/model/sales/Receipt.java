package org.example.model.sales;

import org.example.model.staff.Cashier;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Receipt implements Serializable {
    private static int counter = 1;

    private final int number;
    private final Cashier cashier;
    private final LocalDateTime dateTime;
    private final List<SoldItem> items;

    public Receipt(Cashier cashier) {
        this.number = counter++;
        this.cashier = cashier;
        this.dateTime = LocalDateTime.now();
        this.items = new ArrayList<>();
    }

    public void addItem(SoldItem item) {
        items.add(item);
    }

    public double getTotal() {
        return items.stream().mapToDouble(SoldItem::getTotalPrice).sum();
    }

    public void saveToFile(String folderPath) throws IOException {
        // Създаване на папката, ако не съществува
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String filename = folderPath + "/receipt_" + number + ".txt";
        try (PrintWriter writer = new PrintWriter(filename)) {
            writer.println("Receipt #" + number);
            writer.println("Cashier: " + cashier.getName() + " (ID: " + cashier.getId() + ")");
            writer.println("Date: " + dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println("Items:");
            for (SoldItem item : items) {
                writer.println(" - " + item);
            }
            writer.printf("Total: %.2f%n", getTotal());
        }
    }

    public static Receipt readFromFile(String filepath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            reader.lines().forEach(System.out::println);
            return null; // реална десериализация по-късно
        }
    }

    public int getNumber() {
        return number;
    }
}


