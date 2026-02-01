Feature: Customer Registration
  As a customer
  I want to register with DTU Pay
  So that I can make payments to merchants

  Scenario: Successfully register a new customer
    Given a customer with name "Jane" "Gronsiz" and CPR "011192-1334"
    And the customer has a bank account with balance 1000 kr
    When the customer registers with DTU Pay
    Then the customer registration is successful
    And the customer receives a DTU Pay customer ID

  Scenario: Successfully deregister a customer
    Given a registered customer with name "Renne" "Freen" and CPR "161003-2778"
    When the customer deregisters from DTU Pay
    Then the customer deregistration is successful
