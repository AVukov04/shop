package org.example.model.sales;

import org.example.model.staff.Cashier;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * Prevents a new receipt from reusing a number already present in the target folder.
     * Both text and serialized receipt filenames are considered.
     */
    public static synchronized void ensureNextNumberAfterFiles(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) ->
                name.matches("receipt_\\d+\\.(txt|ser)"));
        if (files == null) {
            return;
        }

        Pattern pattern = Pattern.compile("receipt_(\\d+)\\.(?:txt|ser)");
        int highestNumber = 0;
        for (File file : files) {
            Matcher matcher = pattern.matcher(file.getName());
            if (matcher.matches()) {
                try {
                    highestNumber = Math.max(highestNumber, Integer.parseInt(matcher.group(1)));
                } catch (NumberFormatException ignored) {
                    // Ignore filenames whose numeric part is outside the integer range.
                }
            }
        }
        counter = Math.max(counter, highestNumber + 1);
    }

    public void addItem(SoldItem item) {
        items.add(item);
    }

    public double getTotal() {
        return items.stream().mapToDouble(SoldItem::getTotalPrice).sum();
    }

    public void saveToFile(String folderPath) throws IOException {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String filename = folderPath + "/receipt_" + number + ".txt";
        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"))) {
            writer.println(this); // използва toString() метода
        }
    }

    public static String readFromFile(String filepath) throws IOException {
        return Files.readString(Path.of(filepath), StandardCharsets.UTF_8);
    }

    public void saveAsSerialized(String folderPath) throws IOException {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String filename = folderPath + "/receipt_" + number + ".ser";
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
        }
    }

    public static Receipt loadFromSerialized(String filepath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filepath))) {
            return (Receipt) in.readObject();
        }
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Receipt #").append(number).append("\n");
        sb.append("Cashier: ").append(cashier.getName())
                .append(" (ID: ").append(cashier.getId()).append(")\n");
        sb.append("Date: ").append(dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        sb.append("Items:\n");
        for (SoldItem item : items) {
            sb.append(" - ").append(item).append("\n");
        }
        sb.append(String.format("Total: %.2f", getTotal()));
        return sb.toString();
    }
}


