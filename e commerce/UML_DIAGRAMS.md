# UML Diagrams for E-Commerce Shopping Cart System

## 1. Complete System Class Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                    E-Commerce System                            │
└─────────────────────────────────────────────────────────────────┘

┌──────────────┐
│ECommerceServer│
├──────────────┤
│ -facade      │
│ -gson        │
│ +start()     │
└──────┬───────┘
       │ uses
       ▼
┌──────────────┐
│ECommerceFacade│
├──────────────┤
│ -cart        │
│ -productSubject│
│ +addProduct()│
│ +addToCart() │
│ +applyDiscount()│
│ +checkout()  │
└──────┬───────┘
       │
       ├──────────┬──────────┬──────────┬──────────┬──────────┐
       │          │          │          │          │          │
       ▼          ▼          ▼          ▼          ▼          ▼
┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐
│Builder  │ │Observer │ │Strategy │ │Factory  │ │Facade   │ │Adapter  │
└─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘
       │          │          │          │          │          │
       ▼          ▼          ▼          ▼          ▼          ▼
┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐
│Product  │ │Subject  │ │Discount │ │Product  │ │(self)   │ │Payment  │
│         │ │         │ │Strategy │ │Factory  │ │         │ │Adapter  │
└─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘
```

## 2. Builder Pattern

```
┌─────────────────────────────────────┐
│            Product                  │
├─────────────────────────────────────┤
│ -id: String                         │
│ -name: String                       │
│ -basePrice: double                  │
│ -stock: int                         │
│ -configurations: Map<String,String> │
├─────────────────────────────────────┤
│ +getId(): String                    │
│ +getName(): String                  │
│ +getBasePrice(): double             │
│ +getStock(): int                    │
│ +getConfigurations(): Map           │
└─────────────────────────────────────┘
              ▲
              │ extends
              │
┌─────────────────────────────────────┐
│      Product.Builder                │
├─────────────────────────────────────┤
│ -id: String                         │
│ -name: String                       │
│ -basePrice: double                  │
│ -stock: int                         │
│ -configurations: Map                │
├─────────────────────────────────────┤
│ +basePrice(double): Builder         │
│ +stock(int): Builder                │
│ +addConfiguration(String,String):   │
│   Builder                           │
│ +build(): Product                   │
└─────────────────────────────────────┘
```

## 3. Observer Pattern

```
┌─────────────────────────────────────┐
│      ProductObserver (interface)   │
├─────────────────────────────────────┤
│ +onPriceChanged(Product,double,    │
│   double): void                    │
│ +onStockChanged(Product,int,int):  │
│   void                             │
└──────────────┬─────────────────────┘
               │ implements
       ┌───────┴───────┐
       │               │
┌──────▼──────┐ ┌──────▼──────────────┐
│CartObserver │ │ InventoryManager     │
├─────────────┤ ├─────────────────────┤
│ -cart       │ │ -inventory: Map      │
├─────────────┤ ├─────────────────────┤
│ +onPrice... │ │ +onPriceChanged()  │
│ +onStock... │ │ +onStockChanged()  │
└─────────────┘ └─────────────────────┘
       │               │
       └───────┬───────┘
               │ observes
               ▼
┌─────────────────────────────────────┐
│      ProductSubject                 │
├─────────────────────────────────────┤
│ -observers: List<ProductObserver>   │
├─────────────────────────────────────┤
│ +addObserver(ProductObserver): void│
│ +removeObserver(ProductObserver):   │
│   void                              │
│ +notifyPriceChange(...): void       │
│ +notifyStockChange(...): void       │
└─────────────────────────────────────┘
```

## 4. Strategy Pattern

```
┌─────────────────────────────────────┐
│    DiscountStrategy (interface)     │
├─────────────────────────────────────┤
│ +calculateDiscount(double,int):     │
│   double                           │
│ +getDescription(): String          │
└──────────────┬─────────────────────┘
               │ implements
    ┌──────────┼──────────┐
    │          │          │
┌───▼────┐ ┌───▼────┐ ┌───▼──────────┐
│Percent │ │Fixed   │ │Bulk          │
│Discount│ │Amount  │ │Discount      │
│Strategy│ │Discount│ │Strategy      │
├────────┤ │Strategy│ ├──────────────┤
│-percent│ ├────────┤ │-threshold:int│
│:double │ │-amount:│ │-discount:    │
└────────┘ │ double │ │  double      │
           └────────┘ └──────────────┘
               │
               │ used by
               ▼
┌─────────────────────────────────────┐
│        ShoppingCart                 │
├─────────────────────────────────────┤
│ -items: List<CartItem>              │
│ -discountStrategy: DiscountStrategy │
├─────────────────────────────────────┤
│ +addItem(Product, int): void        │
│ +setDiscountStrategy(...): void    │
│ +calculateTotal(): double          │
└─────────────────────────────────────┘
```

## 5. Factory Pattern

```
┌─────────────────────────────────────┐
│        ProductFactory               │
├─────────────────────────────────────┤
│ +createProduct(String type,        │
│   String id, String name,          │
│   double price, int stock,         │
│   Map configs): Product            │
└──────────────┬─────────────────────┘
               │ uses
               ▼
┌─────────────────────────────────────┐
│        Product.Builder              │
└─────────────────────────────────────┘
```

## 6. Facade Pattern

```
┌─────────────────────────────────────┐
│      ECommerceFacade               │
├─────────────────────────────────────┤
│ -cart: ShoppingCart                │
│ -productSubject: ProductSubject     │
│ -inventoryManager: InventoryManager │
│ -cartObserver: CartObserver        │
│ -products: Map<String,Product>      │
├─────────────────────────────────────┤
│ +addProduct(Product): void          │
│ +addToCart(String, int): void      │
│ +updateProductPrice(...): void     │
│ +applyDiscount(...): void          │
│ +checkout(...): Order              │
└──────────────┬──────────────────────┘
               │ coordinates
    ┌──────────┼──────────┐
    │          │          │
    ▼          ▼          ▼
┌─────────┐ ┌─────────┐ ┌─────────┐
│Cart     │ │Observer │ │Strategy │
└─────────┘ └─────────┘ └─────────┘
```

## 7. Adapter Pattern

```
┌─────────────────────────────────────┐
│    PaymentAdapter (interface)       │
├─────────────────────────────────────┤
│ +processPayment(double, String):    │
│   boolean                          │
│ +getPaymentProvider(): String       │
└──────────────┬─────────────────────┘
               │ implements
       ┌───────┴───────┐
       │               │
┌──────▼──────┐ ┌──────▼──────────────┐
│PayPalAdapter│ │ CreditCardAdapter   │
├─────────────┤ ├─────────────────────┤
│ -payPalService│ │ -creditCardProcessor│
├─────────────┤ ├─────────────────────┤
│ +process... │ │ +processPayment()  │
└─────────────┘ └─────────────────────┘
```

## 8. Order and Email Service

```
┌─────────────────────────────────────┐
│            Order                    │
├─────────────────────────────────────┤
│ -orderId: String                    │
│ -customerEmail: String              │
│ -items: List<CartItem>              │
│ -totalAmount: double                │
│ -paymentMethod: String              │
├─────────────────────────────────────┤
│ +getOrderId(): String               │
│ +getCustomerEmail(): String         │
│ +getItems(): List<CartItem>         │
│ +getTotalAmount(): double           │
└─────────────────────────────────────┘
              ▲
              │ used by
              │
┌─────────────────────────────────────┐
│        EmailService                 │
├─────────────────────────────────────┤
│ -SMTP_HOST: String                  │
│ -SMTP_PORT: String                  │
│ -SENDER_EMAIL: String               │
│ -SENDER_PASSWORD: String            │
├─────────────────────────────────────┤
│ +configure(...): void               │
│ +sendOrderConfirmation(...): boolean│
│ -buildEmailBody(Order): String      │
└─────────────────────────────────────┘
```

## 9. Sequence Diagram - Complete Flow

```
User          Frontend        API Server      Facade        Patterns
 │                │                │            │              │
 │--Browse------->│                │            │              │
 │                │--GET /products->│            │              │
 │                │                │--getProducts()-->│              │
 │                │                │            │--Factory-->Builder
 │                │                │            │<--Product---│
 │                │<--products-----│<--products-│              │
 │<--Display------│                │            │              │
 │                │                │            │              │
 │--Add to Cart-->│                │            │              │
 │                │--POST /cart---->│            │              │
 │                │                │--addToCart()-->│              │
 │                │                │            │--Cart.addItem()│
 │                │                │            │              │
 │--Apply Discount->│                │            │              │
 │                │--POST /discount->│            │              │
 │                │                │--applyDiscount()-->│              │
 │                │                │            │--Cart.setStrategy()│
 │                │                │            │              │
Admin--Update Price->│                │            │              │
 │                │--POST /update-->│            │              │
 │                │                │--updatePrice()-->│              │
 │                │                │            │--Subject.notify()│
 │                │                │            │              │--Observer
 │                │                │            │              │--CartObserver
 │                │                │            │              │--updatePrice()
 │                │                │            │              │
 │                │<--updated------│<--success--│              │
 │<--Notification--│                │            │              │
 │                │                │            │              │
 │--Checkout----->│                │            │              │
 │                │--POST /checkout>│            │              │
 │                │                │--checkout()-->│              │
 │                │                │            │--Adapter.processPayment()│
 │                │                │            │--Order created│
 │                │                │            │--EmailService.send()│
 │                │<--orderId------│<--success--│              │
 │<--Confirmation--│                │            │              │
```

## 10. Checkout Flow Sequence Diagram

```
User    Frontend    API Server    Facade    Adapter    Order    EmailService
 │          │            │           │          │         │          │
 │--Checkout->│            │           │          │         │          │
 │          │--POST------>│           │          │         │          │
 │          │            │--checkout()>│          │         │          │
 │          │            │           │          │         │          │
 │          │            │           │--processPayment()>│         │          │
 │          │            │           │          │--success│         │          │
 │          │            │           │<---------│         │          │
 │          │            │           │          │         │          │
 │          │            │           │--new Order()------>│          │
 │          │            │           │          │         │          │
 │          │            │           │--sendOrderConfirmation()------>│
 │          │            │           │          │         │          │
 │          │            │           │          │         │<--email sent│
 │          │            │           │<---------│         │          │
 │          │            │           │          │         │          │
 │          │<--orderId--│<--order---│          │         │          │
 │<--Success│            │           │          │         │          │
```

## 11. Pattern Interaction Diagram

```
                    ┌─────────────┐
                    │   Facade    │
                    │ (Coordinator)│
                    └──────┬──────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ▼                  ▼                  ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│   Builder    │  │   Observer   │  │   Strategy   │
│              │  │              │  │              │
│ Creates      │  │ Notifies     │  │ Calculates   │
│ Products     │  │ Changes      │  │ Discounts     │
└──────┬───────┘  └──────┬───────┘  └──────┬───────┘
       │                 │                 │
       │                 │                 │
       ▼                 ▼                 ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│   Factory    │  │ CartObserver │  │ ShoppingCart  │
│              │  │              │  │              │
│ Uses Builder │  │ Updates Cart │  │ Uses Strategy │
└──────────────┘  └──────────────┘  └──────────────┘
                           │
                           │
                           ▼
                  ┌──────────────┐
                  │   Adapter    │
                  │              │
                  │ Processes    │
                  │ Payments     │
                  └──────┬───────┘
                         │
                         ▼
                  ┌──────────────┐
                  │    Order     │
                  │              │
                  │ Created on   │
                  │ Checkout     │
                  └──────┬───────┘
                         │
                         ▼
                  ┌──────────────┐
                  │EmailService  │
                  │              │
                  │ Sends        │
                  │ Confirmation │
                  └──────────────┘
```

---

## 12. Class Relationships Summary

### Inheritance Relationships
- `Product.Builder` → builds → `Product`
- `PayPalAdapter` implements `PaymentAdapter`
- `CreditCardAdapter` implements `PaymentAdapter`
- `CartObserver` implements `ProductObserver`
- `InventoryManager` implements `ProductObserver`
- `PercentageDiscountStrategy` implements `DiscountStrategy`
- `FixedAmountDiscountStrategy` implements `DiscountStrategy`
- `BulkDiscountStrategy` implements `DiscountStrategy`

### Composition Relationships
- `ECommerceFacade` contains `ShoppingCart`
- `ECommerceFacade` contains `ProductSubject`
- `ShoppingCart` contains `List<CartItem>`
- `CartItem` contains `Product`
- `Order` contains `List<CartItem>`

### Dependency Relationships
- `ECommerceFacade` uses `ProductFactory`
- `ECommerceFacade` uses `PaymentAdapter`
- `ECommerceFacade` uses `EmailService`
- `ProductFactory` uses `Product.Builder`
- `ShoppingCart` uses `DiscountStrategy`
- `ProductSubject` notifies `ProductObserver`

---

**Note:** These diagrams can be converted to proper UML format using tools like:
- PlantUML
- Draw.io
- Lucidchart
- Visual Paradigm
