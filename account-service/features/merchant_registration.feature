Feature: Merchant registration and management

  Scenario: Successfully register a new merchant
    When a "MerchantRegistrationRequested" event is sent with merchant firstName "Alice", lastName "Business", cpr "999888-7777", bankAccount "DK9998887777", requestId "req-1"
    Then a "MerchantRegistered" event is published
    And the merchant registration is successful
    And the merchant registration response contains a merchant ID

  Scenario: Fail to deregister a non-existing merchant
    When a "MerchantDeregistrationRequested" event is sent for merchant "non-existing-id" with requestId "req-3"
    Then a "MerchantDeregistrationFailed" event is published
    And the merchant deregistration fails with error containing "not found"

  Scenario: Fail to lookup a non-existing merchant
    When a "MerchantLookupRequested" event is sent to lookup merchant "non-existing-id" with requestId "req-5"
    Then a "MerchantLookupFailed" event is published
    And the merchant lookup fails with error containing "not found"

  Scenario: Register then deregister a merchant
    When a "MerchantRegistrationRequested" event is sent with merchant firstName "Charlie", lastName "Shop", cpr "555666-7778", bankAccount "DK5556667778", requestId "req-6"
    Then a "MerchantRegistered" event is published
    And the merchant registration is successful
    When the registered merchant is deregistered with requestId "req-7"
    Then a "MerchantDeregistered" event is published
    And the merchant deregistration is successful

  Scenario: Register then lookup a merchant
    When a "MerchantRegistrationRequested" event is sent with merchant firstName "David", lastName "Store", cpr "222333-4445", bankAccount "DK2223334445", requestId "req-8"
    Then a "MerchantRegistered" event is published
    And the merchant registration is successful
    When the registered merchant is looked up with requestId "req-9"
    Then a "MerchantLookupCompleted" event is published
    And the merchant lookup is successful with bankAccount "DK2223334445"
