Feature: Facade messaging integration

  Scenario: Customer registration request is sent and response received
    When a customer registration request is sent with firstName "John", lastName "Doe", cpr "123456-1234", bankAccount "DK1234567890"
    Then a "CustomerRegistrationRequested" event is published
    When a "CustomerRegistered" response event is received with customerId "c-123"
    Then the customer registration response is successful with customerId "c-123"

  Scenario: Token generation request is sent and response received
    When a token generation request is sent for customer "c-123" with count 3
    Then a "TokensRequested" event is published
    When a "TokenGenerationSucceeded" response event is received with 3 tokens
    Then the token generation response contains 3 tokens

  Scenario: Payment request is sent and response received
    When a payment request is sent with token "tok-123", merchantId "m-456", amount 100.00
    Then a "PaymentInitiated" event is published
    When a "PaymentCompleted" response event is received
    Then the payment response is successful

  Scenario: Manager report request is sent and response received
    When a manager report request is sent
    Then a "ManagerReportRequested" event is published
    When a "ManagerReportCompleted" response event is received with 5 payments
    Then the manager report response contains 5 payments

  Scenario: Customer deregistration request is sent and response received
    When a customer deregistration request is sent for customer "c-123"
    Then a "CustomerDeregistrationRequested" event is published
    When a "CustomerDeregistered" response event is received for deregistration
    Then the customer deregistration response is successful
