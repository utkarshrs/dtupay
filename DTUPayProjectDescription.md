# DTU Pay – Project Description

## 1. Overview

DTU Pay is a microservice-based mobile payment system that enables customers to pay merchants using anonymous, one-time tokens. The system integrates with an external bank via SOAP for money transfers while internally managing accounts, tokens, payments, and reports.

The project is implemented according to the principles taught in the course, including:

* Domain-Driven Design (DDD)
* Hexagonal (Ports & Adapters) Architecture
* Microservices
* RESTful API design
* Asynchronous messaging
* Automated testing with Cucumber and JUnit

Authentication and authorization are intentionally omitted, as required by the project specification.

---

## 2. Functional Scope

The implemented functionality includes:

* Customer and merchant registration with DTU Pay
* Token management with strict business rules:

    * Anonymous, unguessable, one-time-use tokens
    * Maximum of 6 unused tokens per customer
    * Requests allowed only when ≤1 unused token remains
* Payment execution initiated by merchants using a token
* Integration with an external bank (SOAP) for money transfer
* Internal storage of all DTU Pay payment records
* Reporting:

    * Customer payment history
    * Merchant payment history (without customer identity)
    * Manager overview of all payments and total transferred amount

---

## 3. Architecture Overview

### 3.1 High-level Architecture

The system consists of:

* A **Facade service** exposing REST APIs for customers, merchants, and the manager
* Four internal domain microservices:

    * Account Management
    * Token Management
    * Payment Management
    * Report Management
* One system test project for end-to-end testing
* An external SOAP-based bank service

Internal communication between services uses **asynchronous messaging**, while external communication uses **REST** (clients) and **SOAP** (bank).

---

### 3.2 Microservices and Responsibilities

#### 3.2.1 Facade Service (Quarkus)

**Responsibility**

* Acts as the single entry point to DTU Pay
* Exposes three REST facades:

    * Customer API
    * Merchant API
    * Manager API
* Delegates commands to internal services
* Contains no business logic

**REST Paths**

* `/customers/**`
* `/merchants/**`
* `/manager/**`

---

#### 3.2.2 Account Management Service

**Responsibility**

* Registers and deregisters customers and merchants
* Stores DTU Pay identifiers and associated bank account IDs
* Does not validate bank accounts (per specification)

---

#### 3.2.3 Token Management Service

**Responsibility**

* Issues tokens according to business rules
* Maintains token-to-customer mapping (internal only)
* Validates and consumes tokens during payment
* Ensures token anonymity and uniqueness

---

#### 3.2.4 Payment Management Service

**Responsibility**

* Orchestrates payment execution
* Validates merchant and token
* Resolves token to customer
* Calls the external bank SOAP service
* Persists payment records
* Publishes payment events

**Bank Integration**

* Implemented as a SOAP adapter inside this service
* Uses a port (`BankTransferPort`) and adapter (`BankSoapClient`)
* Keeps the design extensible without introducing an extra microservice

---

#### 3.2.5 Report Management Service

**Responsibility**

* Builds read models from payment events
* Generates reports for:

    * Customers
    * Merchants
    * Manager
* Does not communicate with the bank

---

## 4. Messaging Design

Asynchronous messaging is used to decouple services.

### Events

* `CustomerRegistered`
* `MerchantRegistered`
* `TokensIssued`
* `TokenUsed`
* `PaymentCompleted`
* `PaymentFailed`

### Example Flow (Successful Payment)

1. Merchant initiates payment via facade
2. Payment Service validates token and merchant
3. Bank transfer is executed
4. `PaymentCompleted` event is published
5. Reporting Service updates read models
6. Token Service marks token as used

---

## 5. Project Structure

### 5.1 Top-level Structure

```
dtu-pay/
├── docker-compose.yml
├── build-and-test.sh
├── README.md
│
├── facade-service/
├── account-mgmt-service/
├── token-mgmt-service/
├── payment-mgmt-service/
├── report-mgmt-service/
│
├── system-tests/
│
├── openapi/
│   ├── customer-api.yaml
│   ├── merchant-api.yaml
│   └── manager-api.yaml
│
└── docs/
    ├── project_description.md
    └── installation_guide.md
```

Each service is an independent Maven project and Docker container.

---

## 6. Internal Service Structure (Example)

```
payment-mgmt-service/
├── pom.xml
├── Dockerfile
└── src/
    ├── main/
    │   ├── java/
    │   │   └── dtu/pay/payment/
    │   │       ├── domain/
    │   │       │   ├── Payment.java
    │   │       │   ├── PaymentRepository.java
    │   │       │   └── BankTransferPort.java
    │   │       ├── application/
    │   │       │   └── PaymentService.java
    │   │       ├── infrastructure/
    │   │       │   └── soap/
    │   │       │       └── BankSoapClient.java
    │   │       └── messaging/
    │   │           └── PaymentEventPublisher.java
    │   └── resources/
    └── test/
```

This structure follows hexagonal architecture strictly.

---

## 7. System Tests

### System Test Project

```
system-tests/
├── pom.xml
└── src/
    ├── test/
    │   ├── java/
    │   │   └── dtu/pay/systemtests/
    │   │       ├── CustomerAPI.java
    │   │       ├── MerchantAPI.java
    │   │       └── BankTestClient.java
    │   └── resources/
    │       └── features/
    │           ├── successful_payment.feature
    │           ├── token_management.feature
    │           └── reporting.feature
```

Cucumber scenarios drive the development and verify end-to-end behavior.

---

## 8. Project Checklists

### Facade Service

* [ ] Customer registration endpoint
* [ ] Merchant registration endpoint
* [ ] Token request endpoint
* [ ] Payment endpoint
* [ ] Customer report endpoint
* [ ] Merchant report endpoint
* [ ] Manager report endpoint
* [ ] OpenAPI documentation

### Account Management Service

* [ ] Register customer
* [ ] Register merchant
* [ ] Deregister entities
* [ ] In-memory repository
* [ ] Unit tests

### Token Management Service

* [ ] Token generation (unguessable)
* [ ] Token issuance rules enforced
* [ ] Token validation
* [ ] Token consumption
* [ ] Unit tests

### Payment Management Service

* [ ] Payment orchestration
* [ ] Token validation integration
* [ ] SOAP bank adapter
* [ ] Exception handling for failed transfers
* [ ] Event publishing
* [ ] Unit tests

### Report Management Service

* [ ] Payment event consumption
* [ ] Customer report generation
* [ ] Merchant report generation
* [ ] Manager summary report
* [ ] Unit tests

### System Tests

* [ ] Successful payment scenario
* [ ] Token exhaustion scenario
* [ ] Invalid token scenario
* [ ] Reporting scenarios

---

## 9. Design Rationale

The chosen architecture balances:

* Simplicity (exam constraints)
* Clear separation of concerns
* Demonstration of learning objectives

The bank integration is implemented as an adapter rather than a separate service to avoid unnecessary complexity while still respecting hexagonal architecture.

---

## 10. Collaboration and Contributions

All code and documentation include contributor annotations as required. Development followed an incremental, scenario-driven approach with continuous integration.

---

## 11. Conclusion

This project demonstrates a complete, well-structured microservice system that fulfills the DTU Pay requirements with a strong focus on quality, architecture, and testability.

---

## 12. Testing Guide

This section provides step-by-step instructions for running automated tests and performing manual end-to-end testing.

### 12.1 Prerequisites

* Docker and Docker Compose installed
* Java 21 JDK installed
* Maven 3.9+ installed
* All services running via Docker Compose

### 12.2 Starting the System

```bash
# Navigate to project root
cd group8-dtupay

# Build and start all services
docker-compose build
docker-compose up -d

# Verify all services are running
docker-compose ps
```

### 12.3 Running Automated Tests

#### Run test-client Tests (4 scenarios)

```bash
cd test-client
mvn test
```

#### Run end-to-end-tests Tests (11 scenarios)

```bash
cd end-to-end-tests
mvn test
```

### 12.4 Manual End-to-End Testing with Swagger UI

#### Step 1: Create Bank Accounts

The DTU Bank is an external SOAP service. Use the helper utility to create real bank accounts with initial balance:

```bash
cd test-client
mvn compile test-compile exec:java -Dexec.mainClass="dtu.pay.CreateTestAccounts" -Dexec.classpathScope=test -q
```

This outputs ready-to-use JSON with real bank account IDs, for example:

```
✅ Customer Bank Account Created!
   Bank Account ID: e0a032c3-b228-405e-9d7f-06990f70345d
   Name: John Doe
   CPR: 010190-5861
   Balance: 1000 DKK

✅ Merchant Bank Account Created!
   Bank Account ID: 555741f5-36d4-4a81-bcd1-61ededb5e513
   Name: Shop Owner
   CPR: 020280-6130
   Balance: 1000 DKK
```

#### Step 2: Open Swagger UI

Open your browser and navigate to:

```
http://localhost:8080/swagger-ui
```

#### Step 3: Register a Customer

**Endpoint:** `POST /customers`

**Request Body** (use values from Step 1):
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "cpr": "010190-5861",
  "bankAccountNumber": "e0a032c3-b228-405e-9d7f-06990f70345d"
}
```

**Save the returned `customerId`.**

#### Step 4: Register a Merchant

**Endpoint:** `POST /merchants`

**Request Body** (use values from Step 1):
```json
{
  "firstName": "Shop",
  "lastName": "Owner",
  "cpr": "020280-6130",
  "bankAccountNumber": "555741f5-36d4-4a81-bcd1-61ededb5e513"
}
```

**Save the returned `merchantId`.**

#### Step 5: Generate Tokens for the Customer

**Endpoint:** `POST /customers/{customerId}/tokens`

**Path Parameter:** Replace `{customerId}` with the ID from Step 3

**Request Body:**
```json
{
  "tokenCount": 5
}
```

**Copy one token `value` from the response.**

#### Step 6: Make a Payment

**Endpoint:** `POST /merchants/{merchantId}/payments`

**Path Parameter:** Replace `{merchantId}` with the ID from Step 4

**Request Body:**
```json
{
  "token": "<paste-token-value-from-step-5>",
  "amount": 100,
  "description": "Test purchase"
}
```

#### Step 7: Verify with Manager Report

**Endpoint:** `GET /manager/report`

The response should contain the payment showing customer ID, merchant ID, amount, and description.

### 12.5 Summary of Test Endpoints

| Step | Endpoint | Purpose |
|------|----------|---------|
| 1 | `POST /customers` | Register customer → get `customerId` |
| 2 | `POST /merchants` | Register merchant → get `merchantId` |
| 3 | `POST /customers/{customerId}/tokens` | Generate tokens → copy token `value` |
| 4 | `POST /merchants/{merchantId}/payments` | Execute payment using token |
| 5 | `GET /manager/report` | Verify payment was recorded |
| 6 | `GET /customers/{customerId}/report` | View customer payment history |
| 7 | `GET /merchants/{merchantId}/report` | View merchant payment history |

### 12.6 Viewing Service Logs

To debug issues, view individual service logs:

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f facade
docker-compose logs -f account-service
docker-compose logs -f token-service
docker-compose logs -f payment-service
docker-compose logs -f report-service
```

### 12.7 Stopping the System

```bash
docker-compose down
```

---
