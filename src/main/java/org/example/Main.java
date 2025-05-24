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
        // 1. Настройка на магазина
        Shop shop = new Shop();

        // 2. Добавяне на касиер
        Cashier cashier = new Cashier("C001", "Ivan Ivanov", 1200.0);
        shop.addCashier(cashier);

        // 3. Създаване на калкулатори
        PriceCalculator foodCalc = new FoodPriceCalculator(30);     // 30% надценка
        PriceCalculator nonFoodCalc = new NonFoodPriceCalculator(15); // 15% надценка

        // 4. Добавяне на продукти
        Product milk = new FoodProduct("F001", "Milk", 1.00,
                LocalDate.now().plusDays(4), 10, foodCalc);
        Product bread = new FoodProduct("F002", "Bread", 0.80,
                LocalDate.now().plusDays(2), 15, foodCalc);
        Product shampoo = new NonFoodProduct("N001", "Shampoo", 5.00,
                LocalDate.now().plusDays(90), 5, nonFoodCalc);

        shop.addProduct(milk);
        shop.addProduct(bread);
        shop.addProduct(shampoo);

        // 5. Симулиране на продажба
        Map<String, Integer> order = new HashMap<>();
        order.put("F001", 2); // 2 броя Milk
        order.put("N001", 1); // 1 брой Shampoo

        try {
            Receipt receipt = shop.sellProducts(order, "C001", 3, 10.0, "receipts");
            System.out.println("Продажба успешно извършена!");
            System.out.println("Обща сума: " + receipt.getTotal());
        } catch (IOException e) {
            System.out.println("Грешка при записване на бележка: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Грешка при продажба: " + e.getMessage());
        }

        // 6. Извеждане на финанси
        System.out.println("Общ приход: " + shop.calculateTotalIncome());
        System.out.println("Общи разходи (заплати): " + shop.calculateTotalSalaries());
        System.out.println("Разходи по доставки: " + shop.calculateDeliveryCosts());
        System.out.println("Печалба: " + shop.calculateProfit());
    }
}
