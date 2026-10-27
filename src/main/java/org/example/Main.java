package org.example;

import org.example.model.pricing.*;
import org.example.model.product.*;
import org.example.model.sales.Receipt;
import org.example.model.shop.Shop;
import org.example.model.staff.Cashier;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // 1. Setup shop
        Shop shop = new Shop();

        // 2. Add a cashier
        Cashier cashier = new Cashier("C001", "Ivan Ivanov", 1200.0);
        shop.addCashier(cashier);

        // 3. Price calculators
        PriceCalculator foodCalc = new FoodPriceCalculator(30);       // 30% markup
        PriceCalculator nonFoodCalc = new NonFoodPriceCalculator(15); // 15% markup

        // 4. Add products
        Product milk = new FoodProduct("F001", "Milk", 1.00,
                LocalDate.now().plusDays(4), 10, foodCalc);
        Product bread = new FoodProduct("F002", "Bread", 0.80,
                LocalDate.now().plusDays(2), 15, foodCalc);
        Product shampoo = new NonFoodProduct("N001", "Shampoo", 5.00,
                LocalDate.now().plusDays(90), 5, nonFoodCalc);

        shop.addProduct(milk);
        shop.addProduct(bread);
        shop.addProduct(shampoo);

        // 5. Simulate sale
        Map<String, Integer> order = new HashMap<>();
        order.put("F001", 2); // 2 x Milk
        order.put("N001", 1); // 1 x Shampoo

        try {
            Receipt receipt = shop.sellProducts(order, "C001", 3, 10.0, "receipts");

            // 6. Show and save receipt
            System.out.println("Sale completed successfully!");
            System.out.println(receipt); // uses toString()
            System.out.println("Receipt has been saved and serialized.");

            // 7. Load from .ser
            Receipt loaded = Receipt.loadFromSerialized("receipts/receipt_" + receipt.getNumber() + ".ser");
            System.out.println("Loaded receipt total: " + loaded.getTotal());

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // 8. Financial summary
        System.out.println("\n--- Shop Summary ---");
        System.out.println("Total income: " + shop.calculateTotalIncome());
        System.out.println("Total salaries: " + shop.calculateTotalSalaries());
        System.out.println("Total delivery cost: " + shop.calculateDeliveryCosts());
        System.out.println("Profit: " + shop.calculateProfit());

        // 9. Load receipts from folder
        shop.loadReceiptsFromFolder("receipts");
    }
}



