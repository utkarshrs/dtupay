Feature: Token generation and validation

  Scenario: Customer requests tokens successfully
    When the "CustomerRegistered" event is sent with customer id "c-1"
    And the "TokensRequested" event is sent for customer "c-1" with count 3
    Then a "TokenGenerationSucceeded" event was published
    And 3 tokens are generated for customer "c-1"

  Scenario: Deny request for zero tokens
    When the "CustomerRegistered" event is sent with customer id "c-1"
    And the "TokensRequested" event is sent for customer "c-1" with count 0
    Then a "TokenGenerationDenied" event was published
    And the error message contains "Can only request 1 to 5 tokens per request"

  Scenario: Deny request for more than 5 tokens
    When the "CustomerRegistered" event is sent with customer id "c-1"
    And the "TokensRequested" event is sent for customer "c-1" with count 6
    Then a "TokenGenerationDenied" event was published
    And the error message contains "Can only request 1 to 5 tokens per request"

  Scenario: Deny request when customer has more than 1 unused token
    When the "CustomerRegistered" event is sent with customer id "c-1"
    And the "TokensRequested" event is sent for customer "c-1" with count 2
    And the "TokensRequested" event is sent for customer "c-1" with count 1
    Then a "TokenGenerationDenied" event was published
    And the error message contains "Request denied: customer has more than 1 unused token"

  Scenario: Deny request for non-existent customer
    When the "TokensRequested" event is sent for customer "unknown-customer" with count 1
    Then a "TokenGenerationDenied" event was published
    And the error message contains "Customer not found: unknown-customer"

  Scenario: Token validation succeeds
    When the "CustomerRegistered" event is sent with customer id "c-1"
    And the "TokensRequested" event is sent for customer "c-1" with count 1
    And the "PaymentInitiated" event is sent with the generated token, merchant "m-1", amount 100.00, payment id "corr-1"
    Then a "TokenValidated" event was published
    And the token is marked as used for customer "c-1"

  Scenario: Token validation fails for invalid token
    When the "PaymentInitiated" event is sent with token "invalid-token", merchant "m-1", amount 100.00, payment id "corr-1"
    Then a "TokenValidationFailed" event was published
    And the error message contains "Token not found or already used"

  Scenario: Token cannot be used twice
    When the "CustomerRegistered" event is sent with customer id "c-1"
    And the "TokensRequested" event is sent for customer "c-1" with count 1
    And the "PaymentInitiated" event is sent with the generated token, merchant "m-1", amount 100.00, payment id "corr-1"
    And the "PaymentInitiated" event is sent with the same token, merchant "m-1", amount 50.00, payment id "corr-2"
    Then a "TokenValidationFailed" event was published
    And the error message contains "Token not found or already used"
