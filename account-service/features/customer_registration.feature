Feature: Customer registration and management

  Scenario: Successfully register a new customer
    When a "CustomerRegistrationRequested" event is sent with firstName "John", lastName "Doe", cpr "123456-1234", bankAccount "DK1234567890", requestId "req-1"
    Then a "CustomerRegistered" event is published
    And the customer registration is successful
    And the customer registration response contains a customer ID

  Scenario: Fail to deregister a non-existing customer
    When a "CustomerDeregistrationRequested" event is sent for customer "non-existing-id" with requestId "req-3"
    Then a "CustomerDeregistrationFailed" event is published
    And the customer deregistration fails with error containing "not found"

  Scenario: Fail to lookup a non-existing customer
    When a "CustomerLookupRequested" event is sent to lookup customer "non-existing-id" with requestId "req-5"
    Then a "CustomerLookupFailed" event is published
    And the customer lookup fails with error containing "not found"

  Scenario: Register then deregister a customer
    When a "CustomerRegistrationRequested" event is sent with firstName "Jane", lastName "Doe", cpr "654321-4321", bankAccount "DK0987654321", requestId "req-6"
    Then a "CustomerRegistered" event is published
    And the customer registration is successful
    When the registered customer is deregistered with requestId "req-7"
    Then a "CustomerDeregistered" event is published
    And the customer deregistration is successful

  Scenario: Register then lookup a customer
    When a "CustomerRegistrationRequested" event is sent with firstName "Bob", lastName "Smith", cpr "111111-1111", bankAccount "DK1111111111", requestId "req-8"
    Then a "CustomerRegistered" event is published
    And the customer registration is successful
    When the registered customer is looked up with requestId "req-9"
    Then a "CustomerLookupCompleted" event is published
    And the customer lookup is successful with bankAccount "DK1111111111"
