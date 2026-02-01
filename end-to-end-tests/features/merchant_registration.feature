Feature: Merchant Registration
  As a merchant
  I want to register with DTU Pay
  So that I can receive payments from customers

  Scenario: Successfully register a new merchant
    Given a merchant with name "Alice" "Business" and CPR "030390-9876"
    And the merchant has a bank account with balance 5000 kr
    When the merchant registers with DTU Pay
    Then the merchant registration is successful
    And the merchant receives a DTU Pay merchant ID

  Scenario: Successfully deregister a merchant
    Given a registered merchant with name "Bob" "Shop" and CPR "040490-4321"
    When the merchant deregisters from DTU Pay
    Then the merchant deregistration is successful
