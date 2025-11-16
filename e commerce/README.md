# E-Commerce Shopping Cart System

A comprehensive e-commerce application demonstrating the implementation and integration of 6 design patterns: Builder, Observer, Strategy, Factory Method, Facade, and Adapter.

## Design Patterns Implemented

### 1. **Builder Pattern**
- Used in `Product` class for creating products with customizable configurations
- Allows flexible product configuration with various options
- Provides fluent interface for step-by-step object construction

**Key Classes:**
- `Product.java` - Contains Builder inner class

**Example:**
```java
Product product = new Product.Builder("P001", "Smartphone")
    .basePrice(599.99)
    .stock(50)
    .addConfiguration("color", "Black")
    .addConfiguration("memory", "256GB")
    .build();
```

### 2. **Observer Pattern**
- Implemented through `ProductSubject` and `ProductObserver`
- `InventoryManager` and `CartObserver` monitor price and stock changes
- Real-time notifications to all observers when prices or stock change

**Key Classes:**
- `ProductSubject.java`
- `ProductObserver.java`
- `InventoryManager.java`
- `CartObserver.java`

**How it works:**
1. Admin updates product price through API
2. `ProductSubject` notifies all observers
3. `CartObserver` automatically updates cart prices
4. `InventoryManager` logs changes

### 3. **Strategy Pattern**
- Used for flexible discount calculation algorithms
- Multiple implementations: Percentage, Fixed Amount, Bulk

**Key Classes:**
- `DiscountStrategy.java`
- `PercentageDiscountStrategy.java`
- `FixedAmountDiscountStrategy.java`
- `BulkDiscountStrategy.java`

**Example:**
```java
DiscountStrategy strategy = new PercentageDiscountStrategy(10);
cart.setDiscountStrategy(strategy);
double total = cart.calculateTotal(); 
```

### 4. **Factory Method Pattern**
- `ProductFactory` creates products of different types with default configurations
- Automatically adds type-specific configurations

**Key Classes:**
- `ProductFactory.java`

**Example:**
```java
Product product = ProductFactory.createProduct(
    "electronics", "P001", "Smartphone", 599.99, 50, configs
);
```

### 5. **Facade Pattern**
- `ECommerceFacade` simplifies interaction with the system
- Hides complexity of multiple subsystems
- Provides unified interface for all operations

**Key Classes:**
- `ECommerceFacade.java`

**Example:**
```java
ECommerceFacade facade = new ECommerceFacade();
facade.addProduct(product);
facade.addToCart("P001", 2);
facade.applyDiscount(strategy);
facade.checkout("customer@email.com", "creditcard", "details");
```

### 6. **Adapter Pattern**
- `PaymentAdapter` adapts different payment systems to a common interface
- Allows using different payment providers without changing client code

**Key Classes:**
- `PaymentAdapter.java`
- `PayPalAdapter.java`
- `CreditCardAdapter.java`

**Example:**
```java
PaymentAdapter adapter = new PayPalAdapter();
adapter.processPayment(100.0, "payment_details");
```

## Project Structure

```
e-commerce/
├── src/main/java/com/ecommerce/
│   ├── adapter/      # Adapter Pattern
│   ├── api/          # REST API Server
│   ├── cart/         # Shopping Cart
│   ├── facade/       # Facade Pattern
│   ├── factory/      # Factory Pattern
│   ├── model/        # Data Models (Builder, Order)
│   ├── observer/     # Observer Pattern
│   ├── service/      # Email Service
│   └── strategy/     # Strategy Pattern
└── src/main/resources/web/
    ├── index.html
    ├── style.css
    └── app.js
```

## Features

### Core Functionality
- Product browsing with customizable configurations
- Shopping cart management
- Real-time price and inventory updates
- Multiple discount strategies
- Order processing with email confirmation
- Payment processing through adapters

### Email Confirmation
- Automatic email sending after successful checkout
- HTML email template with order details
- Order ID and payment information included

## How to Run

### Prerequisites
- Java 11 or higher
- Maven 3.6+

### Steps

1. **Compile the project:**
   ```bash
   mvn clean compile
   ```

2. **Run the server:**
   
   On Windows:
   ```bash
   run.bat
   ```
   
   On Linux/Mac:
   ```bash
   chmod +x run.sh
   ./run.sh
   ```
   
   Or directly via Maven:
   ```bash
   mvn exec:java
   ```

3. **Open browser:**
   Navigate to: `http://localhost:8080`

## API Endpoints

- `GET /api/products` - Get list of products
- `GET /api/cart` - Get cart contents
- `POST /api/cart` - Add item to cart
- `PUT /api/cart` - Update item quantity
- `DELETE /api/cart?productId=XXX` - Remove item from cart
- `POST /api/discount` - Apply discount strategy
- `POST /api/update` - Update product price or stock
- `POST /api/checkout` - Complete purchase and send email

## Pattern Interactions

1. **Factory → Builder:** Factory uses Builder to create products
2. **Builder → Observer:** Products created via Builder are registered in Observer system
3. **Observer → Strategy:** Price changes trigger cart recalculation with current Strategy
4. **Facade → All Patterns:** Facade coordinates all patterns
5. **Adapter → Payment:** Adapter pattern used for payment processing
6. **Checkout Flow:** Facade → Adapter (payment) → Order creation → EmailService

## Technology Stack

- **Backend:** Java 11
- **Frontend:** HTML, CSS, JavaScript
- **Build Tool:** Maven
- **JSON Processing:** Gson
- **Email:** JavaMail API
- **Web Server:** Java HTTP Server

## Email Configuration

The email service uses Gmail SMTP by default. To configure:

1. Use Gmail App Password (not regular password)
2. Update credentials in `EmailService.java` or use environment variables
3. Configure via `EmailService.configure()` method

## Example Usage Flow

1. **Create Product (Factory + Builder):**
   ```java
   Product product = ProductFactory.createProduct("electronics", "P001", 
       "Smartphone", 599.99, 50, configs);
   facade.addProduct(product);
   ```

2. **Add to Cart:**
   ```java
   facade.addToCart("P001", 2);
   ```

3. **Apply Discount (Strategy):**
   ```java
   DiscountStrategy strategy = new PercentageDiscountStrategy(10);
   facade.applyDiscount(strategy);
   ```

4. **Update Price (Observer):**
   ```java
   facade.updateProductPrice("P001", 549.99);
   ```

5. **Checkout (Adapter + Email):**
   ```java
   Order order = facade.checkout("customer@gmail.com", "card", "details");
   ```

## License

This project is created for educational purposes to demonstrate design patterns.
