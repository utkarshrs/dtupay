package dtu.pay.steps;

import dtu.pay.dto.*;
import dtu.pay.helper.BankHelper;
import dtu.pay.service.FacadeClient;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Shared step definitions used across multiple feature files.
 * Contains common Given steps for customer and merchant registration.
 */
public class SharedSteps {

    private static FacadeClient client;
    private static final BankHelper bankHelper = new BankHelper();

    // Shared state - accessible by other step classes via TestContext
    public static String customerId;
    public static String customerBankAccount;
    public static List<String> customerTokens = new ArrayList<>();
    public static String merchantId;
    public static String merchantBankAccount;
    
    // Track multiple named customers/merchants for complex scenarios
    private static final Map<String, String> namedCustomerIds = new HashMap<>();
    private static final Map<String, String> namedMerchantIds = new HashMap<>();

    // Cleanup tracking
    private static final List<String> registeredCustomerIds = new ArrayList<>();
    private static final List<String> registeredMerchantIds = new ArrayList<>();
    private static final List<String> bankAccountIds = new ArrayList<>();

    @Before
    public void setup() {
        // Initialize client if not already created or if it was closed
        if (client == null) {
            client = new FacadeClient();
        }
    }

    @Given("a registered customer {string} {string} with CPR {string} and balance {int} kr")
    public void aRegisteredCustomerWithBalance(String firstName, String lastName, String cpr, int balance) {
        // Create bank account
        customerBankAccount = bankHelper.createAccount(firstName, lastName, cpr, balance);
        bankAccountIds.add(customerBankAccount);
        
        // Register with DTU Pay
        CustomerRegistration registration = new CustomerRegistration(
                firstName, lastName, cpr, customerBankAccount
        );
        CustomerResponse response = client.registerCustomer(registration);
        assertTrue(response.isSuccess(), "Customer registration should succeed: " + response.getErrorMessage());
        customerId = response.getCustomerId();
        registeredCustomerIds.add(customerId);
        
        // Track by name for complex scenarios
        String key = firstName + " " + lastName;
        namedCustomerIds.put(key, customerId);
    }

    @Given("a registered merchant {string} {string} with CPR {string} and balance {int} kr")
    public void aRegisteredMerchantWithBalance(String firstName, String lastName, String cpr, int balance) {
        // Create bank account
        merchantBankAccount = bankHelper.createAccount(firstName, lastName, cpr, balance);
        bankAccountIds.add(merchantBankAccount);
        
        // Register with DTU Pay
        MerchantRegistration registration = new MerchantRegistration(
                firstName, lastName, cpr, merchantBankAccount
        );
        MerchantResponse response = client.registerMerchant(registration);
        assertTrue(response.isSuccess(), "Merchant registration should succeed: " + response.getErrorMessage());
        merchantId = response.getMerchantId();
        registeredMerchantIds.add(merchantId);
        
        // Track by name for complex scenarios
        String key = firstName + " " + lastName;
        namedMerchantIds.put(key, merchantId);
    }

    @Given("the customer has a valid token")
    public void theCustomerHasAValidToken() {
        TokenResponse tokenResponse = client.requestTokens(customerId, 1);
        assertTrue(tokenResponse.isSuccess(), "Token request should succeed: " + tokenResponse.getErrorMessage());
        customerTokens.addAll(tokenResponse.getTokens());
    }

    public static FacadeClient getClient() {
        return client;
    }

    public static BankHelper getBankHelper() {
        return bankHelper;
    }
    
    public static String getNamedCustomerId(String firstName, String lastName) {
        return namedCustomerIds.get(firstName + " " + lastName);
    }
    
    public static String getNamedMerchantId(String firstName, String lastName) {
        return namedMerchantIds.get(firstName + " " + lastName);
    }

    @After(order = 0) // Run last to ensure client is available for other cleanup hooks
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

        // Deregister merchants
        for (String id : registeredMerchantIds) {
            try {
                client.deregisterMerchant(id);
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
        registeredMerchantIds.clear();

        // Retire bank accounts
        for (String accountId : bankAccountIds) {
            try {
                bankHelper.retireAccount(accountId);
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
        bankAccountIds.clear();

        // Close HTTP client and set to null so it's recreated for next scenario
        try {
            if (client != null) {
                client.close();
                client = null;
            }
        } catch (Exception e) {
            // Ignore cleanup errors
        }

        // Clear shared state
        customerId = null;
        customerBankAccount = null;
        customerTokens.clear();
        merchantId = null;
        merchantBankAccount = null;
        namedCustomerIds.clear();
        namedMerchantIds.clear();
    }
}
