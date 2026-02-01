package dtu.pay.steps;

import dtu.pay.dto.*;
import dtu.pay.helper.BankHelper;
import dtu.pay.service.FacadeClient;
import io.cucumber.java.After;
import io.cucumber.java.en.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TokenSteps {

    private final FacadeClient client = new FacadeClient();
    private final BankHelper bankHelper = new BankHelper();

    // Test state
    private String customerId;
    private String customerBankAccount;
    private TokenResponse tokenResponse;
    private List<String> currentTokens = new ArrayList<>();

    // Cleanup tracking
    private final List<String> registeredCustomerIds = new ArrayList<>();
    private final List<String> bankAccountIds = new ArrayList<>();

    @Given("a registered customer {string} {string} with CPR {string}")
    public void aRegisteredCustomerWithCpr(String firstName, String lastName, String cpr) {
        // Create bank account
        customerBankAccount = bankHelper.createAccount(firstName, lastName, cpr, 1000);
        bankAccountIds.add(customerBankAccount);
        
        // Register with DTU Pay
        CustomerRegistration registration = new CustomerRegistration(
                firstName, lastName, cpr, customerBankAccount
        );
        CustomerResponse response = client.registerCustomer(registration);
        assertTrue(response.isSuccess(), "Customer registration should succeed: " + response.getErrorMessage());
        customerId = response.getCustomerId();
        registeredCustomerIds.add(customerId);
    }

    @Given("the customer has {int} unused token")
    public void theCustomerHasUnusedTokens(int count) {
        tokenResponse = client.requestTokens(customerId, count);
        assertTrue(tokenResponse.isSuccess(), "Initial token request should succeed");
        currentTokens.addAll(tokenResponse.getTokens());
    }

    @When("the customer requests {int} tokens")
    public void theCustomerRequestsTokens(int count) {
        tokenResponse = client.requestTokens(customerId, count);
        if (tokenResponse.isSuccess()) {
            currentTokens.addAll(tokenResponse.getTokens());
        }
    }

    @Then("the token request is successful")
    public void theTokenRequestIsSuccessful() {
        assertTrue(tokenResponse.isSuccess(),
                "Token request should be successful but got error: " + tokenResponse.getErrorMessage());
    }

    @Then("the customer receives {int} tokens")
    public void theCustomerReceivesTokens(int expectedCount) {
        assertNotNull(tokenResponse.getTokens(), "Tokens list should not be null");
        assertEquals(expectedCount, tokenResponse.getTokens().size(),
                "Should receive " + expectedCount + " tokens");
    }

    // Getters for shared state
    public String getCustomerId() {
        return customerId;
    }

    public List<String> getCurrentTokens() {
        return currentTokens;
    }

    @After(order = 1) // Run before SharedSteps cleanup
    public void cleanup() {
        // Deregister customers
        for (String id : registeredCustomerIds) {
            try {
                client.deregisterCustomer(id);
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
        registeredCustomerIds.clear();

        // Retire bank accounts
        for (String accountId : bankAccountIds) {
            try {
                bankHelper.retireAccount(accountId);
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
        bankAccountIds.clear();
        
        // Close HTTP client (instance-specific)
        try {
            client.close();
        } catch (Exception e) {
            // Ignore cleanup errors
        }
        
        currentTokens.clear();
    }
}
