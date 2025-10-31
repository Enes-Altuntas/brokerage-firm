# 🏦 Brokerage Backend (SAGA Chareography, Inbox/Outbox, CQRS)

A minimal **brokerage backend** for handling BUY/SELL orders, cancellations, and matches.  
Built with **Spring Boot**, **Kafka**, **PostgreSQL (Debezium CDC)**, **ElasticSearch**, **Redis**, and a **mock-auth Gateway**.

---

## 🧪 Run
```
docker-compose up -d
```

Then run these 2 endpoints from postman:
- **Debezzium Order DB Initialize** → To connect Debezzium to Order DB
- **Debezzium Asset DB Initialize** → To connect Debezzium to Asset DB

**Then you can freely use other postman collections to run test!**

There are 3 mock users, 1 Admin and 2 customer.

In postman, you can test them with access-token.

**For Admin:**
- Put **token-admin** as Bearer token

**For Customer A:**
- Put **token-customer-a** as Bearer token

**For Customer B:**
- Put **token-customer-b** as Bearer token

With the start of the **order command service**, assets will be automatically uploaded to both customers with the **flyway migration** in the database.

---
##  🧠 What's Missing !

- More **compensation events** can be developed after tests.
- Develop a real **auth service** or implement **keycloak** to api-gw
- May develop a **notifications service** or implement alert manager like **grafana** for **DLT**
- For more reliability may implement **Circuit Breaker** pattern
---
## 📈 Highlights

- Asynchronous, fault-tolerant, role-aware trading backend.
- Scales horizontally; consistent under concurrency.
- Lightweight but production-structured.

---

## 🧱 Architecture

### 🧩 Microservices

| Service | Responsibility |
|----------|----------------|
| **Order Command Service** | Handles order creation, cancellation, and matching (admin). Publishes domain events. |
| **Order Query Service** | Maintains the read model (CQRS) in ElasticSearch for fast queries. |
| **Asset Service** | Validates balances, manages usable and total asset sizes, and updates holdings. |
| **Kafka** | Transports all domain events between services. |
| **Redis** | Provides distributed locks to avoid concurrent state conflicts. |
| **PostgreSQL + Debezium** | Stores business data and outbox events. Debezium CDC streams events to Kafka. |
| **ElasticSearch** | Serves read-side projections for queries and reporting. |

### ⚙️ Technology Stack

| Component | Technology                   |
|------------|------------------------------|
| **Language** | Java 21                      |
| **Framework** | Spring Boot 3.x              |
| **Database** | PostgreSQL with Debezium CDC |
| **Message Broker** | Apache Kafka                 |
| **Cache / Locking** | Redis                        |
| **Read Model** | ElasticSearch                |
| **Testing** | JUnit 5 - Mockito            |

---

## 🔄 Workflow Summaries

### 🟩 **Order Creation Flow**
1. **User** creates order.
2. **OrderService** saves it as `INIT` and publishes `ORDER_CREATED`.
3. **AssetService** validates:
    - For `BUY`: checks TRY usable size.
    - For `SELL`: checks asset usable size.
4. If balance is sufficient → `ORDER_VALIDATED`, else → `ORDER_REJECTED`.
5. **OrderService** updates state accordingly and ElasticSearch syncs via CDC.

---

### 🟦 **Order Cancel Flow**
1. **User/Admin** triggers cancel.
2. **OrderService** updates state → `CANCEL_REQUESTED` and publishes `ORDER_CANCEL_REQUESTED`.
3. **AssetService**:
    - Restores usable size or TRY.
    - Publishes `ORDER_CANCEL_CONFIRMED` on success.
    - Publishes `ORDER_CANCEL_REJECTED` if update fails (not a compensation event).
4. **OrderService**:
    - `CANCEL_CONFIRMED` → state = `CANCELED`.
    - `ORDER_CANCEL_REJECTED` → state = `PENDING`.

---

### 🟧 **Admin Match Flow (BUY vs SELL Pair)**
1. Admin selects one BUY and one SELL order.
2. **OrderService** publishes `ORDER_MATCH_REQUESTED`.
3. **AssetService**:
    - Decreases SELLER’s stock.
    - Increases BUYER’s stock.
    - Adjusts TRY balances.
    - Publishes `ORDER_MATCHED`.
4. **OrderService** updates both orders → `MATCHED` OR `PARTIALLY_MATCHED`.