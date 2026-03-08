# Online Bookstore - Java EE Backend

A robust backend system for managing books, customers, shopping carts, and orders through secure RESTful APIs. Built with Java EE technologies deployed on WildFly Application Server.

## Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                    Docker Compose                            │
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │   WildFly    │  │  PostgreSQL  │  │    Redis     │       │
│  │  (App Server)│──│  (Database)  │  │   (Cache)    │       │
│  │  Port: 8080  │  │  Port: 5432  │  │  Port: 6379  │       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
└──────────────────────────────────────────────────────────────┘
```

## Technology Stack

| Component                        | Technology                                              |
|----------------------------------|---------------------------------------------------------|
| REST APIs                        | JAX-RS                                                  |
| Business Logic Layer             | EJBs / CDI                                              |
| Persistence Layer                | JPA (Hibernate)                                         |
| Asynchronous Messaging           | JMS (order confirmations, inventory updates, email)     |
| Database                         | PostgreSQL                                              |
| Schema Management & Seed Data    | Liquibase                                               |
| Cache                            | Redis (temporary cart storage)                          |
| Application Server               | WildFly 26                                              |
| Java Version                     | Java 8+                                                 |
| Containerization                 | Docker / Docker Compose                                 |
| Testing                          | JUnit 5, Mockito                                        |
| Build Tool                       | Maven                                                   |

## Project Structure

```
online-bookstore/
├── pom.xml                          # Maven build configuration
├── Dockerfile                       # WildFly application image
├── docker-compose.yml               # Multi-container orchestration
├── docker/
│   └── wildfly/
│       ├── configure-wildfly.cli    # WildFly CLI: datasource, JMS, Redis config
│       ├── postgresql-42.6.0.jar    # PostgreSQL JDBC driver (download required)
│       └── modules/org/postgresql/main/
│           └── module.xml           # WildFly module definition
├── src/
│   ├── main/
│   │   ├── java/com/bookstore/
│   │   │   ├── config/             # JAX-RS Application config
│   │   │   ├── model/              # JPA Entities & POJOs
│   │   │   │   ├── Book.java
│   │   │   │   ├── BookCategory.java
│   │   │   │   ├── Customer.java
│   │   │   │   ├── Order.java
│   │   │   │   ├── OrderItem.java
│   │   │   │   ├── OrderStatus.java
│   │   │   │   ├── PaymentMethod.java
│   │   │   │   ├── Cart.java       # Redis-stored (not JPA entity)
│   │   │   │   └── CartItem.java   # Redis-stored (not JPA entity)
│   │   │   ├── repository/         # Data Access Layer (EJB DAOs)
│   │   │   │   ├── BookRepository.java
│   │   │   │   ├── CustomerRepository.java
│   │   │   │   └── OrderRepository.java
│   │   │   ├── service/            # Business Logic (EJB/CDI Services)
│   │   │   │   ├── BookService.java
│   │   │   │   ├── CustomerService.java
│   │   │   │   ├── CartService.java
│   │   │   │   ├── OrderService.java
│   │   │   │   ├── RedisCartStore.java
│   │   │   │   └── EmailNotificationService.java
│   │   │   ├── jms/                # JMS Messaging
│   │   │   │   ├── OrderMessageProducer.java
│   │   │   │   └── WarehouseMessageListener.java
│   │   │   └── rest/               # JAX-RS REST Endpoints
│   │   │       ├── BookResource.java
│   │   │       ├── CustomerResource.java
│   │   │       ├── CartResource.java
│   │   │       ├── OrderResource.java
│   │   │       └── dto/
│   │   │           ├── CreateOrderRequest.java
│   │   │           ├── UpdateOrderStatusRequest.java
│   │   │           └── AddToCartRequest.java
│   │   ├── resources/
│   │   │   ├── META-INF/
│   │   │   │   └── persistence.xml  # JPA persistence unit config
│   │   │   └── db/changelog/        # Liquibase migrations
│   │   │       ├── db.changelog-master.xml
│   │   │       ├── 001-create-tables.xml
│   │   │       └── 002-seed-data.xml
│   │   └── webapp/WEB-INF/
│   │       └── beans.xml            # CDI configuration
│   └── test/
│       └── java/com/bookstore/
│           ├── model/               # Entity unit tests
│           ├── service/             # Service unit tests (Mockito)
│           └── rest/                # REST endpoint unit tests (Mockito)
└── README.md
```

## Core Features

### 1. Books Management
- CRUD operations for books
- Properties: Name, ISBN, Date Created, Publication House, Category, Price, Stock Quantity
- Filter by category

### 2. Customers Management
- CRUD operations for customers
- Properties: First Name, Last Name, Email, Mobile Phone, Address
- Lookup by email

### 3. Carts Management (Redis-cached)
- Carts are **not** persisted in the database
- Stored in Redis cache with a 24-hour TTL
- Add/remove items, view cart, clear cart

### 4. Orders Management
- Create orders from a customer's cart
- Order includes: books, delivery address, total price, price without VAT (20%), payment method, status
- Update order status (PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
- Email notifications on status changes (stub logger implementation)
- JMS warehouse notifications when book stock is depleted

## REST API Endpoints

### Books API (`/api/books`)

| Method | Path                | Description              |
|--------|---------------------|--------------------------|
| POST   | `/api/books`        | Create a new book        |
| GET    | `/api/books`        | List all books           |
| GET    | `/api/books?category=HORROR` | Filter by category |
| GET    | `/api/books/{id}`   | Get book by ID           |
| GET    | `/api/books/isbn/{isbn}` | Get book by ISBN    |
| PUT    | `/api/books/{id}`   | Update a book            |
| DELETE | `/api/books/{id}`   | Delete a book            |

### Customers API (`/api/customers`)

| Method | Path                          | Description              |
|--------|-------------------------------|--------------------------|
| POST   | `/api/customers`              | Create a new customer    |
| GET    | `/api/customers`              | List all customers       |
| GET    | `/api/customers/{id}`         | Get customer by ID       |
| GET    | `/api/customers/email/{email}`| Get customer by email    |
| PUT    | `/api/customers/{id}`         | Update a customer        |
| DELETE | `/api/customers/{id}`         | Delete a customer        |

### Carts API (`/api/carts`)

| Method | Path                                    | Description              |
|--------|-----------------------------------------|--------------------------|
| GET    | `/api/carts/{customerId}`               | Get customer's cart      |
| POST   | `/api/carts/{customerId}/items`         | Add item to cart         |
| DELETE | `/api/carts/{customerId}/items/{bookId}`| Remove item from cart    |
| DELETE | `/api/carts/{customerId}`               | Clear entire cart        |

### Orders API (`/api/orders`)

| Method | Path                        | Description                   |
|--------|-----------------------------|-------------------------------|
| POST   | `/api/orders`               | Create order from cart        |
| GET    | `/api/orders`               | List all orders               |
| GET    | `/api/orders?customerId=1`  | Filter by customer            |
| GET    | `/api/orders?status=PENDING`| Filter by status              |
| GET    | `/api/orders/{id}`          | Get order by ID               |
| PUT    | `/api/orders/{id}/status`   | Update order status           |

## Prerequisites

- **Java 8** or higher
- **Maven 3.6+**
- **Docker** and **Docker Compose** (for containerized deployment)

## Build & Test

### Build the project
```bash
mvn clean package
```

### Run unit tests only
```bash
mvn test
```

The project includes **126 unit tests** covering:
- Model entity tests (Book, Customer, Cart, Order)
- Service layer tests with Mockito mocks (BookService, CustomerService, CartService, OrderService, EmailNotificationService)
- REST endpoint tests with Mockito mocks (BookResource, CustomerResource, CartResource, OrderResource)

## Docker Deployment

### 1. Download PostgreSQL JDBC Driver

Before building the Docker image, download the PostgreSQL JDBC driver:
```bash
curl -L -o docker/wildfly/postgresql-42.6.0.jar \
  https://jdbc.postgresql.org/download/postgresql-42.6.0.jar
```

### 2. Build the WAR file
```bash
mvn clean package -DskipTests
```

### 3. Start all services
```bash
docker-compose up --build
```

This starts:
- **WildFly** on port `8080` (application) and `9990` (management)
- **PostgreSQL** on port `5432`
- **Redis** on port `6379`

### 4. Access the API
```
http://localhost:8080/online-bookstore/api/books
http://localhost:8080/online-bookstore/api/customers
http://localhost:8080/online-bookstore/api/carts/{customerId}
http://localhost:8080/online-bookstore/api/orders
```

### Stop all services
```bash
docker-compose down
```

### Stop and remove volumes
```bash
docker-compose down -v
```

## Example API Requests

### Create a Book
```bash
curl -X POST http://localhost:8080/online-bookstore/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "name": "The Great Gatsby",
    "isbn": "978-0743273565",
    "publicationHouse": "Scribner",
    "category": "OTHER",
    "price": 10.99,
    "stockQuantity": 200
  }'
```

### Create a Customer
```bash
curl -X POST http://localhost:8080/online-bookstore/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Alice",
    "lastName": "Wonder",
    "email": "alice@example.com",
    "mobilePhone": "+1-555-0303",
    "address": "789 Elm Street, Chicago, IL 60601"
  }'
```

### Add Item to Cart
```bash
curl -X POST http://localhost:8080/online-bookstore/api/carts/1/items \
  -H "Content-Type: application/json" \
  -d '{
    "bookId": 1,
    "quantity": 2
  }'
```

### Create an Order from Cart
```bash
curl -X POST http://localhost:8080/online-bookstore/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "deliveryAddress": "456 Oak Avenue, Boston, MA 02101",
    "paymentMethod": "CREDIT_CARD"
  }'
```

### Update Order Status
```bash
curl -X PUT http://localhost:8080/online-bookstore/api/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "SHIPPED"
  }'
```

## Design Decisions

1. **Cart Storage**: Carts are stored in Redis with a 24-hour TTL, not in the database, for performance and scalability.
2. **VAT Calculation**: A 20% VAT rate is applied to order totals.
3. **Email Notifications**: Implemented as a stub (logs only) since no SMTP server is required per requirements.
4. **JMS Warehouse Notifications**: Sent via ActiveMQ Artemis (WildFly's built-in JMS provider) when book stock reaches zero.
5. **Liquibase Migrations**: Schema and seed data are managed through versioned XML changelogs.

## Seed Data

The Liquibase seed data includes:
- **5 books**: The Shining, Dune, The Hobbit, Murder on the Orient Express, A Brief History of Time
- **3 customers**: John Doe, Jane Smith, Bob Johnson
