Feature: Payment processing based on PaymentInitiated events

  Scenario: Successful payment results in PaymentCompleted event
    When the "PaymentInitiated" event is sent for payment "p-1", token "t-1", merchant "m-1", amount 100.00, description "test payment", timestamp "2025-01-01T10:00:00Z"
    And the "CustomerBankAccountResolved" event is sent for payment "p-1", customer "c-1", from account "a-1"
    And the "MerchantBankAccountResolved" event is sent for payment "p-1", merchant "m-1", to account "a-2"
    Then a "PaymentCompleted" event was published
    And the completed payment has amount 100.00, customer "c-1", merchant "m-1", description "test payment"

  Scenario: Payment fails due to insufficient balance
    Given the bank transfer is configured to fail
    When the "PaymentInitiated" event is sent for payment "p-2", token "t-2", merchant "m-2", amount 200.00, description "insufficient balance test", timestamp "2025-01-01T11:00:00Z"
    And the "CustomerBankAccountResolved" event is sent for payment "p-2", customer "c-2", from account "a-3"
    And the "MerchantBankAccountResolved" event is sent for payment "p-2", merchant "m-2", to account "a-4"
    Then a "PaymentFailed" event was published
    And the error message contains "Insufficient balance"

  Scenario: Payment fails due to invalid token
    When the "PaymentInitiated" event is sent for payment "p-3", token "t-invalid", merchant "m-3", amount 150.00, description "invalid token test", timestamp "2025-01-01T12:00:00Z"
    And the "TokenValidationFailed" event is sent for payment "p-3", token "t-invalid", reason "Token does not exist"
    Then a "PaymentFailed" event was published
    And the error message contains "Invalid token"
