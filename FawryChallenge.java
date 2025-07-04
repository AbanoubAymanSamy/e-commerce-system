package fawrychallenge;

import java.util.*;

public class FawryChallenge {

    public static void main(String[] args) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 3); // Valid future expiry date
        Date validDate = cal.getTime();

        // Date expiredDate = ... // I was testing expired product but removed it for now

        // Define sample products
        Product cheese = new ExpirableShippableProduct("Cheese", 100, 5, validDate, 0.2);
        Product biscuits = new ExpirableShippableProduct("Biscuits", 150, 2, validDate, 0.7);
        Product scratchCard = new NonExpirableProduct("Scratch Card", 50, 10);

        // Create a customer
        Customer customer = new Customer("Abanoub", 1000);

        // Create a cart
        Cart cart = new Cart();

        // Add products to cart (quantity should not exceed available)
        cart.add(cheese, 2);
        cart.add(biscuits, 1);
        cart.add(scratchCard, 1);

        System.out.println("Processing checkout...");

        Checkout.process(customer, cart);
    }
}

// Interface for all shippable products
interface Shippable {
    String getName();
    double getWeight(); // weight in kg
}

// Abstract class for all types of products
abstract class Product {
    protected String name;
    protected double price;
    protected int quantity;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public abstract boolean isExpired(); // to be implemented in subclasses

    public boolean isAvailable(int requestedQty) {
        return quantity >= requestedQty && !isExpired();
    }

    public void reduceQuantity(int qty) {
        quantity -= qty;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean requiresShipping() {
        return this instanceof Shippable;
    }
}

// Products that have expiry dates
class ExpirableProduct extends Product {
    private Date expiryDate;

    public ExpirableProduct(String name, double price, int quantity, Date expiryDate) {
        super(name, price, quantity);
        this.expiryDate = expiryDate;
    }

    @Override
    public boolean isExpired() {
        return new Date().after(expiryDate);
    }
}

// Products that don't expire
class NonExpirableProduct extends Product {
    public NonExpirableProduct(String name, double price, int quantity) {
        super(name, price, quantity);
    }

    @Override
    public boolean isExpired() {
        return false;
    }
}

// Shippable product without expiry
class ShippableProduct extends Product implements Shippable {
    private double weight;

    public ShippableProduct(String name, double price, int quantity, double weight) {
        super(name, price, quantity);
        this.weight = weight;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    public double getWeight() {
        return weight;
    }
}

// Product that both expires and needs shipping
class ExpirableShippableProduct extends ExpirableProduct implements Shippable {
    private double weight;

    public ExpirableShippableProduct(String name, double price, int quantity, Date expiryDate, double weight) {
        super(name, price, quantity, expiryDate);
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }
}

// Represents a product with quantity in the cart
class CartItem {
    Product product;
    int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }

    public boolean isShippable() {
        return product instanceof Shippable;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
}

// Represents a customer with a name and balance
class Customer {
    private String name;
    private double balance;

    public Customer(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void deduct(double amount) {
        balance -= amount;
    }

    public String getName() {
        return name;
    }
}

// Represents the shopping cart
class Cart {
    private List<CartItem> items = new ArrayList<>();

    // Add product to the cart
    public void add(Product product, int qty) {
        if (qty <= 0) {
            System.out.println("Invalid quantity.");
            return;
        }
        if (!product.isAvailable(qty)) {
            System.out.println("Product is either out of stock or expired.");
            return;
        }
        items.add(new CartItem(product, qty));
    }

    public List<CartItem> getItems() {
        return items;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public double getSubtotal() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public double getShippingCost() {
        return getShippableItems().isEmpty() ? 0 : 30;
    }

    // Get all products that need shipping
    public List<Shippable> getShippableItems() {
        List<Shippable> list = new ArrayList<>();
        for (CartItem item : items) {
            if (item.isShippable()) {
                for (int i = 0; i < item.getQuantity(); i++) {
                    list.add((Shippable) item.getProduct());
                }
            }
        }
        return list;
    }
}

// Responsible for printing shipping info
class ShippingService {
    public static void shipItems(List<Shippable> items) {
        if (items.isEmpty()) return;

        System.out.println("** Shipment notice **");
        double totalWeight = 0;
        Map<String, Integer> itemCounts = new HashMap<>();
        Map<String, Double> weights = new HashMap<>();

        for (Shippable item : items) {
            itemCounts.put(item.getName(), itemCounts.getOrDefault(item.getName(), 0) + 1);
            weights.put(item.getName(), item.getWeight());
            totalWeight += item.getWeight();
        }

        for (String name : itemCounts.keySet()) {
            System.out.println(itemCounts.get(name) + "x " + name + " " + (weights.get(name) * 1000) + "g");
        }

        System.out.printf("Total package weight %.1fkg\n", totalWeight);
    }
}

// Handles checkout logic
class Checkout {
    public static void process(Customer customer, Cart cart) {
        if (cart.isEmpty()) {
            System.out.println("Error: Cart is empty.");
            return;
        }

        double subtotal = cart.getSubtotal();
        double shipping = cart.getShippingCost();
        double total = subtotal + shipping;

        if (customer.getBalance() < total) {
            System.out.println("Error: Insufficient balance.");
            return;
        }

        for (CartItem item : cart.getItems()) {
            if (!item.getProduct().isAvailable(item.getQuantity())) {
                System.out.println("Error: One or more products are out of stock or expired.");
                return;
            }
        }

        // Deduct product quantities
        for (CartItem item : cart.getItems()) {
            item.getProduct().reduceQuantity(item.getQuantity());
        }

        // Deduct from customer balance
        customer.deduct(total);

        // Handle shipping
        ShippingService.shipItems(cart.getShippableItems());

        // Print receipt
        System.out.println("** Checkout receipt **");
        for (CartItem item : cart.getItems()) {
            System.out.printf("%dx %s %.0f\n", item.getQuantity(), item.getProduct().getName(), item.getTotalPrice());
        }

        System.out.println("----------------------");
        System.out.println("Subtotal " + subtotal);
        System.out.println("Shipping " + shipping);
        System.out.println("Amount " + total);
        System.out.println("Balance after payment: " + customer.getBalance());
    }
}
