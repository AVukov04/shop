package org.example.model.pricing;

import org.example.model.product.Product;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NonFoodPriceCalculatorTest {

    @Test
    public void testCalculatePrice_NotExpired() {
        Product product = mock(Product.class);

        when(product.isExpired(any())).thenReturn(false);
        when(product.getDeliveryPrice()).thenReturn(10.0);

        NonFoodPriceCalculator calculator = new NonFoodPriceCalculator(15.0); // 15% markup

        double price = calculator.calculatePrice(product, LocalDate.now(), 0, 0);

        // base price = 10 * 1.15 = 11.5
        assertEquals(11.5, price, 0.001);
    }

    @Test
    public void testCalculatePrice_Expired() {
        Product product = mock(Product.class);

        when(product.isExpired(any())).thenReturn(true);

        NonFoodPriceCalculator calculator = new NonFoodPriceCalculator(15.0);

        double price = calculator.calculatePrice(product, LocalDate.now(), 0, 0);

        assertEquals(0.0, price, 0.001);
    }

    @Test
    public void testCalculatePrice_ZeroDeliveryPrice() {
        Product product = mock(Product.class);

        when(product.isExpired(any())).thenReturn(false);
        when(product.getDeliveryPrice()).thenReturn(0.0);

        NonFoodPriceCalculator calculator = new NonFoodPriceCalculator(15.0);

        double price = calculator.calculatePrice(product, LocalDate.now(), 0, 0);

        assertEquals(0.0, price, 0.001);
    }
}

