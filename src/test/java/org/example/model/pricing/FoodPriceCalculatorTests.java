package org.example.model.pricing;

import org.example.model.product.Product;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FoodPriceCalculatorTests {
    @Test
    public void testCalculatePrice_NotExpired_NoDiscount() {
        Product product = mock(Product.class);

        when(product.isExpired(any())).thenReturn(false);
        when(product.getDeliveryPrice()).thenReturn(10.0);
        when(product.getExpirationDate()).thenReturn(LocalDate.now().plusDays(10));

        FoodPriceCalculator calculator = new FoodPriceCalculator(20.0); // 20% markup

        double price = calculator.calculatePrice(product, LocalDate.now(), 3, 10);

        // base price = 10 * 1.2 = 12, no discount because expiry > 3 days
        assertEquals(12.0, price, 0.001);
    }

    @Test
    public void testCalculatePrice_ExpiringSoon_DiscountApplied() {
        Product product = mock(Product.class);

        when(product.isExpired(any())).thenReturn(false);
        when(product.getDeliveryPrice()).thenReturn(10.0);
        when(product.getExpirationDate()).thenReturn(LocalDate.now().plusDays(2));

        FoodPriceCalculator calculator = new FoodPriceCalculator(20.0);

        double price = calculator.calculatePrice(product, LocalDate.now(), 3, 10);

        // base price before discount = 12, discount 10% applied -> 12 * 0.9 = 10.8
        assertEquals(10.8, price, 0.001);
    }

    @Test
    public void testCalculatePrice_Expired_ProductPriceZero() {
        Product product = mock(Product.class);

        when(product.isExpired(any())).thenReturn(true);

        FoodPriceCalculator calculator = new FoodPriceCalculator(20.0);

        double price = calculator.calculatePrice(product, LocalDate.now(), 3, 10);

        assertEquals(0.0, price, 0.001);
    }
}