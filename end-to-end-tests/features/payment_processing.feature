Feature: Payment Processing
  As a merchant
  I want to process payments using customer tokens
  So that I can receive money for goods and services

  Scenario: Successful payment
    Given a registered customer "Peter" "Payer" with CPR "070790-3333" and balance 1000 kr
    And the customer has a valid token
    And a registered merchant "Mary" "Merchant" with CPR "080890-4444" and balance 500 kr
    When the merchant initiates a payment of 100 kr using the customer's token
    Then the payment is successful
    And the customer's bank balance is 900 kr
    And the merchant's bank balance is 600 kr

  Scenario: Successful payment with description
    Given a registered customer "Paul" "Buyer" with CPR "090990-5555" and balance 2000 kr
    And the customer has a valid token
    And a registered merchant "Mike" "Seller" with CPR "101090-6666" and balance 1000 kr
    When the merchant initiates a payment of 250 kr with description "Coffee purchase"
    Then the payment is successful
    And the payment amount is 250 kr

  Scenario: Payment fails with insufficient balance
    Given a registered customer "Bob" "Broke" with CPR "110190-7777" and balance 50 kr
    And the customer has a valid token
    And a registered merchant "Sam" "Seller" with CPR "120290-8888" and balance 500 kr
    When the merchant initiates a payment of 100 kr using the customer's token
    Then the payment fails
    And the error message contains "Insufficient balance"
    And the customer's bank balance is 50 kr
    And the merchant's bank balance is 500 kr

  Scenario: Payment fails with invalid token
    Given a registered customer "Alice" "Payer" with CPR "130390-9999" and balance 1000 kr
    And a registered merchant "Tom" "Trader" with CPR "140490-1111" and balance 500 kr
    When the merchant initiates a payment of 100 kr using invalid token "invalid-token-123"
    Then the payment fails
    And the error message contains "Invalid Token: Token not found or already used"
    And the customer's bank balance is 1000 kr
    And the merchant's bank balance is 500 kr
