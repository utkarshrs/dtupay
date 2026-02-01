Feature: Payment Concurrency
  As the DTU Pay system
  I want to handle concurrent payment requests correctly
  So that multiple payments can be processed simultaneously without errors

  Scenario: Multiple customers pay different merchants concurrently
    Given 3 registered customers with 1000 kr balance and 1 token each
    And 3 registered merchants with 500 kr balance
    When all customers initiate payments of 100 kr to their respective merchants concurrently
    Then all 3 payments should succeed
    And all customers should have 900 kr balance
    And all merchants should have 600 kr balance

  Scenario: Multiple customers pay the same merchant concurrently
    Given 5 registered customers with 1000 kr balance and 1 token each
    And 1 registered merchant with 500 kr balance
    When all 5 customers initiate payments of 50 kr to the same merchant concurrently
    Then all 5 payments should succeed
    And all customers should have 950 kr balance
    And the merchant should have 750 kr balance

  Scenario: One customer makes multiple payments concurrently with different tokens
    Given 1 registered customer with 2000 kr balance and 3 tokens
    And 3 registered merchants with 500 kr balance
    When the customer initiates 3 concurrent payments of 100 kr using different tokens
    Then all 3 payments should succeed
    And the customer should have 1700 kr balance
    And all merchants should have 600 kr balance
