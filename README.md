# 🛒 Fawry Internship Challenge - E-Commerce System

## 👨‍💻 Developed by:
**Abanoub Ayman Samy**

## 📌 Description:
This project is a console-based e-commerce system developed in Java as part of the **Fawry Rise Journey Full Stack Internship Challenge**.

It simulates the core flow of an online store:
- Adding products (some expirable, some shippable)
- Adding items to cart
- Calculating subtotal and shipping
- Validating product availability and expiry
- Completing checkout and printing receipts

---

## 🚀 Features Implemented:

- ✅ Define products with name, price, and quantity
- ✅ Support for **expirable products** (e.g. Cheese, Biscuits)
- ✅ Support for **non-expirable products** (e.g. Mobile scratch card)
- ✅ Support for **shippable products** with weight
- ✅ Prevent adding unavailable or expired items to cart
- ✅ Cart checkout with:
  - Subtotal
  - Shipping fees
  - Total paid amount
  - Updated customer balance
- ✅ Handle corner cases:
  - Empty cart
  - Expired product
  - Insufficient balance
  - Out-of-stock items
- ✅ Shipment notice includes:
  - List of items with weight
  - Total package weight

---

## 🧪 Sample Execution:

```java
cart.add(cheese, 2);
cart.add(biscuits, 1);
cart.add(scratchCard, 1);
checkout(customer, cart);
