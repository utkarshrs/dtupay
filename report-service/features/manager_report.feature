Feature: Manager report based on completed payments

  Scenario: Manager sees all completed payments
    When the "PaymentCompleted" event is sent for token "t-1", customer "c-1", merchant "m-1", amount 100.00, description "payment one", timestamp "2025-01-01T10:00:00Z"
    And the "PaymentCompleted" event is sent for token "t-2", customer "c-2", merchant "m-1", amount 200.50, description "payment two", timestamp "2025-01-02T11:00:00Z"
    And the "PaymentCompleted" event is sent for token "t-3", customer "c-1", merchant "m-2", amount 300.75, description "another payment", timestamp "2025-01-03T12:00:00Z"
    When the "ManagerReportRequested" event is sent with id "req-1"
    Then the manager report contains 3 payments
    And a "ManagerReportGenerated" event was published
