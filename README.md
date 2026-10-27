# Java Shop Simulation

A small Java application that models a shop's stock, product pricing, sales, receipts, and basic financial reporting. The project was built as a Java/Gradle exercise focused on object-oriented design, exception handling, file I/O, serialization, and unit testing.

## Features

- Models food and non-food products with IDs, names, delivery prices, expiry dates, and stock quantities.
- Calculates selling prices using category-specific markups.
- Applies a configurable discount to food products approaching their expiry date.
- Rejects expired products and invalid sale quantities.
- Checks stock before completing an order and reports the missing quantity through `InsufficientStockException`.
- Creates receipts containing a receipt number, cashier, timestamp, purchased items, quantities, unit prices, and total.
- Saves each receipt as a readable UTF-8 text file and as a serialized Java object.
- Reads receipt text files and deserializes saved receipts.
- Tracks receipts and calculates sales income, cashier salary costs, delivery costs, and a basic profit figure.
- Includes JUnit 5 tests for pricing, sales, stock validation, receipts, and serialization.

## Technology

- Java 21
- Gradle
- JUnit 5
- Mockito (test dependency)

## Requirements

- JDK 21
- No separate Gradle installation is required; the project includes the Gradle Wrapper.

## Run the application

On Windows:

```powershell
.\gradlew.bat run
```

On macOS/Linux:

```bash
./gradlew run
```

The example application creates sample products and a cashier, performs a sale, prints the receipt, saves the receipt files in a `receipts/` directory, and displays a financial summary.

## Run tests

On Windows:

```powershell
.\gradlew.bat test
```

On macOS/Linux:

```bash
./gradlew test
```

## Receipt files

Each receipt is saved as two files in the selected output folder:

- `receipt_<number>.txt` — human-readable receipt text.
- `receipt_<number>.ser` — serialized Java `Receipt` object.

The receipt numbering logic checks existing receipt filenames before a new sale so that a number already present in the target folder is not reused. Loading the same serialized receipt folder repeatedly does not add duplicate receipt numbers to the current `Shop` instance.

## Summary

This is an educational console project. The current state does not separately represent physical checkout counters or validate whether a customer has enough money to pay. The profit calculation is a simplified exercise-level calculation based on recorded sales income minus cashier salaries and recorded delivery costs; it is not a full accounting app.
