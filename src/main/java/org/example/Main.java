package org.example;

import org.example.model.product.FoodProduct;
import org.example.model.product.NonFoodProduct;
import org.example.model.pricing.FoodPriceCalculator;
import org.example.model.pricing.NonFoodPriceCalculator;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        int expiryThresholdDays = 5;
        double discountPercent = 20.0;


        FoodPriceCalculator foodCalculator = new FoodPriceCalculator(30);       // 30% надценка
        NonFoodPriceCalculator nonFoodCalculator = new NonFoodPriceCalculator(15); // 15% надценка


        FoodProduct milk = new FoodProduct(
                "F001",
                "Milk",
                1.0,
                LocalDate.now().plusDays(3),
                10,
                foodCalculator
        );

        NonFoodProduct shampoo = new NonFoodProduct(
                "N001",
                "Shampoo",
                5.0,
                LocalDate.now().plusDays(30),
                5,
                nonFoodCalculator
        );

        LocalDate today = LocalDate.now();


        double milkPrice = milk.calculateSellingPrice(today, expiryThresholdDays, discountPercent);
        double shampooPrice = shampoo.calculateSellingPrice(today, expiryThresholdDays, discountPercent);


        System.out.println(milk.getName() + " selling price: " + milkPrice);
        System.out.println(shampoo.getName() + " selling price: " + shampooPrice);
    }
}