Feature: Reporting
  As a user of DTU Pay
  I want to view payment reports with appropriate data visibility
  So that I can track payment history according to my role

  Scenario: Customer views their payment report with correct data visibility
    Given a registered customer "Carol" "Customer" with CPR "111190-7777" and balance 1000 kr
    And a registered merchant "Mark" "Market" with CPR "121290-8888" and balance 500 kr
    And the customer has made a successful payment of 50 kr to the merchant with description "Coffee"
    And the customer has made a successful payment of 75 kr to the merchant with description "Lunch"
    When the customer requests their payment report
    Then the customer report contains 2 payments
    And the customer report shows payment with amount 50 kr, description "Coffee"
    And the customer report shows payment with amount 75 kr, description "Lunch"
    And the customer report includes merchant ID for each payment
    And the customer report includes token for each payment
    And the customer report does not include customer ID

  Scenario: Merchant views their payment report with correct data visibility
    Given a registered customer "Dan" "Shopper" with CPR "131390-9999" and balance 1000 kr
    And a registered merchant "Sara" "Store" with CPR "141490-0000" and balance 500 kr
    And the customer has made a successful payment of 100 kr to the merchant with description "Groceries"
    And the customer has made a successful payment of 150 kr to the merchant with description "Electronics"
    When the merchant requests their payment report
    Then the merchant report contains 2 payments
    And the merchant report shows payment with amount 100 kr, description "Groceries"
    And the merchant report shows payment with amount 150 kr, description "Electronics"
    And the merchant report includes token for each payment
    And the merchant report does not include customer ID
    And the merchant report does not include merchant ID

  Scenario: Manager views overall report with complete data visibility
    Given a registered customer "Alice" "Anderson" with CPR "010190-1234" and balance 2000 kr
    And a registered customer "Bob" "Brown" with CPR "020290-5678" and balance 1500 kr
    And a registered merchant "Charlie" "Shop" with CPR "030390-9012" and balance 1000 kr
    And a registered merchant "Diana" "Store" with CPR "040490-3456" and balance 800 kr
    And customer "Alice" "Anderson" has made a successful payment of 50 kr to merchant "Charlie" "Shop" with description "Item A"
    And customer "Alice" "Anderson" has made a successful payment of 75 kr to merchant "Diana" "Store" with description "Item B"
    And customer "Bob" "Brown" has made a successful payment of 100 kr to merchant "Charlie" "Shop" with description "Item C"
    And customer "Bob" "Brown" has made a successful payment of 125 kr to merchant "Diana" "Store" with description "Item D"
    When the manager requests the overall report
    Then the manager report contains at least 4 payments
    And the manager report includes customer ID for each payment
    And the manager report includes merchant ID for each payment
    And the manager report includes token for each payment
    And the manager report includes amount for each payment
    And the manager report includes description for each payment
