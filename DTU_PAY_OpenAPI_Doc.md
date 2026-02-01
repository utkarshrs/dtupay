# DTU Pay REST API Documentation

## Overview

DTU Pay is a microservice-based mobile payment system that enables customers to pay merchants using anonymous, one-time tokens. This document describes the REST API exposed by the Facade service.

**Base URL**: `http://localhost:8080`

**Interactive Documentation**: `http://localhost:8080/swagger-ui`

**OpenAPI Specification**: `http://localhost:8080/openapi`

## Authentication

No authentication is required (intentionally omitted per specification).

## API Endpoints

### Customer Management

#### Register Customer

Register a new customer with DTU Pay using their bank account details.

**Endpoint**: `POST /customers`

**Request Body**:
```json
{
  "firstName": "string",
  "lastName": "string",
  "cpr": "string",
  "bankAccountNumber": "string"
}
```

**Success Response** (201 Created):
```json
{
  "customerId": "uuid-string",
  "success": true,
  "errorMessage": null
}
```

**Error Responses**:
- `400 Bad Request`: Invalid registration data (missing fields, invalid bank account)
- `409 Conflict`: Customer with this bank account already exists

**Example**:
```bash
curl -X POST http://localhost:8080/customers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "cpr": "1234567890",
    "bankAccountNumber": "1234-5678"
  }'
```

---

#### Deregister Customer

Remove a customer from DTU Pay. All unused tokens will be invalidated.

**Endpoint**: `DELETE /customers/{customerId}`

**Path Parameters**:
- `customerId` (required): The customer's DTU Pay ID

**Success Response** (200 OK):
```json
{
  "customerId": "uuid-string",
  "success": true,
  "errorMessage": null
}
```

**Error Response** (404 Not Found):
```json
{
  "customerId": null,
  "success": false,
  "errorMessage": "Customer not found"
}
```

**Example**:
```bash
curl -X DELETE http://localhost:8080/customers/abc-123-def
```

---

#### Request Payment Tokens

Generate anonymous, one-time payment tokens for a customer.

**Business Rules**:
- Maximum 6 unused tokens per customer
- Can only request when ≤1 unused token remains
- Request 1-5 tokens at a time

**Endpoint**: `POST /customers/{customerId}/tokens`

**Path Parameters**:
- `customerId` (required): The customer's DTU Pay ID

**Request Body**:
```json
{
  "tokenCount": 5
}
```

**Success Response** (201 Created):
```json
{
  "tokens": [
    "token-1-uuid",
    "token-2-uuid",
    "token-3-uuid",
    "token-4-uuid",
    "token-5-uuid"
  ],
  "success": true,
  "errorMessage": null
}
```

**Error Response** (400 Bad Request):
```json
{
  "tokens": [],
  "success": false,
  "errorMessage": "Customer has too many unused tokens"
}
```

**Example**:
```bash
curl -X POST http://localhost:8080/customers/abc-123-def/tokens \
  -H "Content-Type: application/json" \
  -d '{"tokenCount": 5}'
```

---

#### Get Customer Payment Report

Retrieve payment history for a customer. Shows all payments made by this customer.

**Endpoint**: `GET /customers/{customerId}/report`

**Path Parameters**:
- `customerId` (required): The customer's DTU Pay ID

**Success Response** (200 OK):
```json
{
  "customerId": "abc-123-def",
  "payments": [
    {
      "paymentId": "payment-uuid-1",
      "merchantId": "merchant-uuid",
      "amount": 100.50,
      "description": "Coffee",
      "timestamp": "2026-01-22T15:30:00Z"
    }
  ],
  "success": true,
  "errorMessage": null
}
```

**Error Response** (500 Internal Server Error):
```json
{
  "customerId": "abc-123-def",
  "payments": [],
  "success": false,
  "errorMessage": "Failed to retrieve report"
}
```

**Example**:
```bash
curl http://localhost:8080/customers/abc-123-def/report
```

---

### Merchant Management

#### Register Merchant

Register a new merchant with DTU Pay using their bank account details.

**Endpoint**: `POST /merchants`

**Request Body**:
```json
{
  "firstName": "string",
  "lastName": "string",
  "cpr": "string",
  "bankAccountNumber": "string"
}
```

**Success Response** (201 Created):
```json
{
  "merchantId": "uuid-string",
  "success": true,
  "errorMessage": null
}
```

**Error Response** (400 Bad Request):
```json
{
  "merchantId": null,
  "success": false,
  "errorMessage": "Invalid registration data"
}
```

**Example**:
```bash
curl -X POST http://localhost:8080/merchants \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Smith",
    "cpr": "0987654321",
    "bankAccountNumber": "8765-4321"
  }'
```

---

#### Deregister Merchant

Remove a merchant from DTU Pay. The merchant will no longer be able to receive payments.

**Endpoint**: `DELETE /merchants/{merchantId}`

**Path Parameters**:
- `merchantId` (required): The merchant's DTU Pay ID

**Success Response** (200 OK):
```json
{
  "merchantId": "uuid-string",
  "success": true,
  "errorMessage": null
}
```

**Error Response** (404 Not Found):
```json
{
  "merchantId": null,
  "success": false,
  "errorMessage": "Merchant not found"
}
```

**Example**:
```bash
curl -X DELETE http://localhost:8080/merchants/xyz-789-uvw
```

---

#### Process Payment

Initiate a payment from a customer to a merchant using a one-time token. The payment transfers money from the customer's bank account to the merchant's bank account via the external bank service.

**Endpoint**: `POST /merchants/{merchantId}/payments`

**Path Parameters**:
- `merchantId` (required): The merchant's DTU Pay ID

**Request Body**:
```json
{
  "token": "customer-token-uuid",
  "amount": 100.50,
  "description": "Coffee and pastry"
}
```

**Success Response** (201 Created):
```json
{
  "paymentId": "payment-uuid",
  "amount": 100.50,
  "success": true,
  "errorMessage": null
}
```

**Error Responses** (400 Bad Request):
```json
{
  "paymentId": null,
  "amount": null,
  "success": false,
  "errorMessage": "Invalid token"
}
```

Common error messages:
- "Invalid token"
- "Token already used"
- "Merchant not found"
- "Insufficient funds"
- "Bank transfer failed"

**Example**:
```bash
curl -X POST http://localhost:8080/merchants/xyz-789-uvw/payments \
  -H "Content-Type: application/json" \
  -d '{
    "token": "token-abc-123",
    "amount": 100.50,
    "description": "Coffee and pastry"
  }'
```

---

#### Get Merchant Payment Report

Retrieve payment history for a merchant. Shows all payments received by this merchant.

**Note**: Customer identity is NOT included in merchant reports for privacy.

**Endpoint**: `GET /merchants/{merchantId}/report`

**Path Parameters**:
- `merchantId` (required): The merchant's DTU Pay ID

**Success Response** (200 OK):
```json
{
  "merchantId": "xyz-789-uvw",
  "payments": [
    {
      "paymentId": "payment-uuid-1",
      "amount": 100.50,
      "description": "Coffee and pastry",
      "timestamp": "2026-01-22T15:30:00Z"
    }
  ],
  "success": true,
  "errorMessage": null
}
```

**Error Response** (500 Internal Server Error):
```json
{
  "merchantId": "xyz-789-uvw",
  "payments": [],
  "success": false,
  "errorMessage": "Failed to retrieve report"
}
```

**Example**:
```bash
curl http://localhost:8080/merchants/xyz-789-uvw/report
```

---

### Manager Reporting

#### Get System Payment Report

Retrieve all payments in the DTU Pay system. Shows complete payment history including customer IDs, merchant IDs, amounts, descriptions, and timestamps. Also includes the total amount transferred through the system.

**This endpoint is for system administrators only.**

**Endpoint**: `GET /manager/report`

**Success Response** (200 OK):
```json
{
  "payments": [
    {
      "paymentId": "payment-uuid-1",
      "customerId": "customer-uuid",
      "merchantId": "merchant-uuid",
      "amount": 100.50,
      "description": "Coffee and pastry",
      "timestamp": "2026-01-22T15:30:00Z"
    },
    {
      "paymentId": "payment-uuid-2",
      "customerId": "customer-uuid-2",
      "merchantId": "merchant-uuid-2",
      "amount": 250.00,
      "description": "Lunch",
      "timestamp": "2026-01-22T16:00:00Z"
    }
  ],
  "totalAmount": 350.50,
  "success": true,
  "errorMessage": null
}
```

**Error Response** (500 Internal Server Error):
```json
{
  "errorMessage": "Failed to retrieve report",
  "statusCode": 500
}
```

**Example**:
```bash
curl http://localhost:8080/manager/report
```

---

## Data Models

### CustomerRegistrationRequest
```json
{
  "firstName": "string (required)",
  "lastName": "string (required)",
  "cpr": "string (required)",
  "bankAccountNumber": "string (required)"
}
```

### MerchantRegistrationRequest
```json
{
  "firstName": "string (required)",
  "lastName": "string (required)",
  "cpr": "string (required)",
  "bankAccountNumber": "string (required)"
}
```

### TokenRequest
```json
{
  "tokenCount": "integer (1-5, required)"
}
```

### PaymentRequest
```json
{
  "token": "string (required)",
  "amount": "decimal (required)",
  "description": "string (optional)"
}
```

### CustomerResponse
```json
{
  "customerId": "string (UUID)",
  "success": "boolean",
  "errorMessage": "string (null on success)"
}
```

### MerchantResponse
```json
{
  "merchantId": "string (UUID)",
  "success": "boolean",
  "errorMessage": "string (null on success)"
}
```

### TokenResponse
```json
{
  "tokens": ["string (array of UUIDs)"],
  "success": "boolean",
  "errorMessage": "string (null on success)"
}
```

### PaymentResponse
```json
{
  "paymentId": "string (UUID)",
  "amount": "decimal",
  "success": "boolean",
  "errorMessage": "string (null on success)"
}
```

### Payment (in reports)
```json
{
  "paymentId": "string (UUID)",
  "customerId": "string (UUID, only in manager/customer reports)",
  "merchantId": "string (UUID, only in manager/merchant reports)",
  "amount": "decimal",
  "description": "string",
  "timestamp": "string (ISO 8601 datetime)"
}
```

---

## Business Rules

### Token Management
- Customers can have a maximum of 6 unused tokens
- Token requests are only allowed when ≤1 unused token remains
- Each token request must be for 1-5 tokens
- Tokens are anonymous, unguessable (UUID), and single-use
- Tokens are invalidated when customer is deregistered

### Payment Processing
- Tokens can only be used once
- Payments are processed through the external bank service (SOAP)
- Payment amounts must be positive
- Both customer and merchant must be registered

### Reporting
- Customer reports show all payments made by the customer (includes merchant ID)
- Merchant reports show all payments received (does NOT include customer ID for privacy)
- Manager reports show all payments in the system (includes both customer and merchant IDs)

---

## Error Handling

All endpoints return consistent error responses:

```json
{
  "success": false,
  "errorMessage": "Description of what went wrong"
}
```

Common HTTP status codes:
- `200 OK`: Successful GET request
- `201 Created`: Successful POST request (resource created)
- `400 Bad Request`: Invalid input or business rule violation
- `404 Not Found`: Resource not found
- `409 Conflict`: Resource already exists
- `500 Internal Server Error`: Server-side error

---

## Testing the API

### Using cURL

See examples in each endpoint section above.

### Using Swagger UI

1. Start the services: `docker-compose up -d`
2. Open browser: http://localhost:8080/swagger-ui
3. Click on any endpoint to expand it
4. Click "Try it out"
5. Fill in parameters and request body
6. Click "Execute"

### Using Postman

Import the OpenAPI specification from http://localhost:8080/openapi into Postman to automatically generate a collection with all endpoints.

---

## Architecture Notes

### Asynchronous Processing

The Facade service uses asynchronous messaging (RabbitMQ) to communicate with backend services:
- Account Service (customer/merchant registration)
- Token Service (token generation)
- Payment Service (payment processing and bank integration)
- Report Service (payment reporting)

The Facade correlates requests and responses using correlation IDs, making the asynchronous communication transparent to API clients.

### External Dependencies

- **RabbitMQ**: Message broker for inter-service communication (port 5672)
- **Bank Service**: External SOAP service for money transfers (http://fm-00.compute.dtu.dk/services/BankService)

---

## Getting Started

1. **Start all services**:
   ```bash
   docker-compose up -d
   ```

2. **Verify services are running**:
   ```bash
   docker-compose ps
   ```

3. **Check Swagger UI**:
   Open http://localhost:8080/swagger-ui in your browser

4. **Register a customer**:
   ```bash
   curl -X POST http://localhost:8080/customers \
     -H "Content-Type: application/json" \
     -d '{
       "firstName": "John",
       "lastName": "Doe",
       "cpr": "1234567890",
       "bankAccountNumber": "1234-5678"
     }'
   ```

5. **Register a merchant**:
   ```bash
   curl -X POST http://localhost:8080/merchants \
     -H "Content-Type: application/json" \
     -d '{
       "firstName": "Jane",
       "lastName": "Smith",
       "cpr": "0987654321",
       "bankAccountNumber": "8765-4321"
     }'
   ```

6. **Request tokens** (use customerId from step 4):
   ```bash
   curl -X POST http://localhost:8080/customers/{customerId}/tokens \
     -H "Content-Type: application/json" \
     -d '{"tokenCount": 5}'
   ```

7. **Make a payment** (use merchantId from step 5 and a token from step 6):
   ```bash
   curl -X POST http://localhost:8080/merchants/{merchantId}/payments \
     -H "Content-Type: application/json" \
     -d '{
       "token": "{token-from-step-6}",
       "amount": 100.50,
       "description": "Test payment"
     }'
   ```

---
