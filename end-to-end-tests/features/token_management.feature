Feature: Token Management
  As a customer
  I want to request and use tokens
  So that I can make anonymous payments

  Scenario: Successfully request tokens
    Given a registered customer "Tom" "Token" with CPR "050590-1111"
    When the customer requests 3 tokens
    Then the token request is successful
    And the customer receives 3 tokens

  Scenario: Request tokens when customer has 1 unused token
    Given a registered customer "Tim" "Tokens" with CPR "060690-2222"
    And the customer has 1 unused token
    When the customer requests 5 tokens
    Then the token request is successful
    And the customer receives 5 tokens
